/**
 * Licensed to jclouds, Inc. (jclouds) under one or more
 * contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  jclouds licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jclouds.openstack.nova.v1_1.compute;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicReference;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;

import org.jclouds.Constants;
import org.jclouds.collect.Memoized;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.callables.RunScriptOnNode;
import org.jclouds.compute.domain.Hardware;
import org.jclouds.compute.domain.Image;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.internal.BaseComputeService;
import org.jclouds.compute.internal.PersistNodeCredentials;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.compute.reference.ComputeServiceConstants.Timeouts;
import org.jclouds.compute.strategy.CreateNodesInGroupThenAddToSet;
import org.jclouds.compute.strategy.DestroyNodeStrategy;
import org.jclouds.compute.strategy.GetNodeMetadataStrategy;
import org.jclouds.compute.strategy.InitializeRunScriptOnNodeOrPlaceInBadMap;
import org.jclouds.compute.strategy.ListNodesStrategy;
import org.jclouds.compute.strategy.RebootNodeStrategy;
import org.jclouds.compute.strategy.ResumeNodeStrategy;
import org.jclouds.compute.strategy.SuspendNodeStrategy;
import org.jclouds.domain.Credentials;
import org.jclouds.domain.Location;
import org.jclouds.openstack.nova.v1_1.NovaClient;
import org.jclouds.openstack.nova.v1_1.compute.options.NovaTemplateOptions;
import org.jclouds.openstack.nova.v1_1.domain.SecurityGroup;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.SecurityGroupInZone;
import org.jclouds.openstack.nova.v1_1.domain.zonescoped.ZoneAndName;
import org.jclouds.openstack.nova.v1_1.extensions.SecurityGroupClient;
import org.jclouds.openstack.nova.v1_1.predicates.SecurityGroupPredicates;
import org.jclouds.scriptbuilder.functions.InitAdminAccess;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.base.Supplier;
import com.google.common.cache.LoadingCache;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Multimap;

/**
 * @author Adrian Cole
 */
@Singleton
public class NovaComputeService extends BaseComputeService {
   private final NovaClient novaClient;
   private final LoadingCache<ZoneAndName, SecurityGroupInZone> securityGroupMap;
   private final Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByZoneId;

   @Inject
   protected NovaComputeService(ComputeServiceContext context, Map<String, Credentials> credentialStore,
            @Memoized Supplier<Set<? extends Image>> images, @Memoized Supplier<Set<? extends Hardware>> sizes,
            @Memoized Supplier<Set<? extends Location>> locations, ListNodesStrategy listNodesStrategy,
            GetNodeMetadataStrategy getNodeMetadataStrategy,
            CreateNodesInGroupThenAddToSet runNodesAndAddToSetStrategy, RebootNodeStrategy rebootNodeStrategy,
            DestroyNodeStrategy destroyNodeStrategy, ResumeNodeStrategy startNodeStrategy,
            SuspendNodeStrategy stopNodeStrategy, Provider<TemplateBuilder> templateBuilderProvider,
            Provider<TemplateOptions> templateOptionsProvider,
            @Named("NODE_RUNNING") Predicate<AtomicReference<NodeMetadata>> nodeRunning,
            @Named("NODE_TERMINATED") Predicate<AtomicReference<NodeMetadata>> nodeTerminated,
            @Named("NODE_SUSPENDED") Predicate<AtomicReference<NodeMetadata>> nodeSuspended,
            InitializeRunScriptOnNodeOrPlaceInBadMap.Factory initScriptRunnerFactory,
            RunScriptOnNode.Factory runScriptOnNodeFactory, InitAdminAccess initAdminAccess,
            PersistNodeCredentials persistNodeCredentials, Timeouts timeouts,
            @Named(Constants.PROPERTY_USER_THREADS) ExecutorService executor, NovaClient novaClient,
            LoadingCache<ZoneAndName, SecurityGroupInZone> securityGroupMap,
            Function<Set<? extends NodeMetadata>, Multimap<String, String>> orphanedGroupsByZoneId) {
      super(context, credentialStore, images, sizes, locations, listNodesStrategy, getNodeMetadataStrategy,
               runNodesAndAddToSetStrategy, rebootNodeStrategy, destroyNodeStrategy, startNodeStrategy,
               stopNodeStrategy, templateBuilderProvider, templateOptionsProvider, nodeRunning, nodeTerminated,
               nodeSuspended, initScriptRunnerFactory, initAdminAccess, runScriptOnNodeFactory, persistNodeCredentials,
               timeouts, executor);
      this.novaClient = checkNotNull(novaClient, "novaClient");
      this.securityGroupMap = checkNotNull(securityGroupMap, "securityGroupMap");
      this.orphanedGroupsByZoneId = checkNotNull(orphanedGroupsByZoneId, "orphanedGroupsByZoneId");
   }

   @Override
   protected void cleanUpIncidentalResourcesOfDeadNodes(Set<? extends NodeMetadata> deadNodes) {
      Multimap<String, String> zoneToZoneAndGroupNames = orphanedGroupsByZoneId.apply(deadNodes);
      for (String zoneId : zoneToZoneAndGroupNames.keySet()) {
         cleanOrphanedGroupsInZone(ImmutableSet.copyOf(zoneToZoneAndGroupNames.get(zoneId)), zoneId);
      }
   }

   protected void cleanOrphanedGroupsInZone(Set<String> groups, String zoneId) {
      cleanupOrphanedSecurityGroupsInZone(groups, zoneId);
   }

   private void cleanupOrphanedSecurityGroupsInZone(Set<String> groups, String zoneId) {
      Optional<SecurityGroupClient> securityGroupClient = novaClient.getSecurityGroupExtensionForZone(zoneId);
      if (securityGroupClient.isPresent()) {
         for (String group : groups) {
            for (SecurityGroup securityGroup : Iterables.filter(securityGroupClient.get().listSecurityGroups(),
                     SecurityGroupPredicates.nameEquals("jclouds#" + group))) {
               ZoneAndName zoneAndName = ZoneAndName.fromZoneAndName(zoneId, securityGroup.getName());
               logger.debug(">> deleting securityGroup(%s)", zoneAndName);
               securityGroupClient.get().deleteSecurityGroup(securityGroup.getId());
               // TODO: test this clear happens
               securityGroupMap.invalidate(zoneAndName);
               logger.debug("<< deleted securityGroup(%s)", zoneAndName);
            }
         }
      }
   }

   /**
    * returns template options, except of type {@link NovaTemplateOptions}.
    */
   @Override
   public NovaTemplateOptions templateOptions() {
      return NovaTemplateOptions.class.cast(super.templateOptions());
   }

}
