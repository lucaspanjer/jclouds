/**
 *
 * Copyright (C) 2011 Cloud Conscious, LLC. <info@cloudconscious.com>
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package org.jclouds.aws.ec2.domain;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Arrays;
import java.util.Set;

import javax.annotation.Nullable;

import org.jclouds.ec2.domain.BlockDeviceMapping;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapEBSSnapshotToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapEphemeralDeviceToDevice;
import org.jclouds.ec2.domain.BlockDeviceMapping.MapNewVolumeToDevice;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;

/**
 * 
 * @see <a href=
 *      "http://docs.amazonwebservices.com/AWSEC2/latest/APIReference/ApiReference-query-RequestSpotInstances.html"
 *      />
 * @author Adrian Cole
 */
public class LaunchSpecification {

   public static Builder builder() {
      return new Builder();
   }

   public static class Builder {
      protected ImmutableSet.Builder<String> groupIds = ImmutableSet.<String> builder();
      protected String imageId;
      protected String instanceType;
      protected String kernelId;
      protected String keyName;
      protected String availabilityZone;
      protected String ramdiskId;
      protected Boolean monitoringEnabled;
      protected ImmutableSet.Builder<BlockDeviceMapping> blockDeviceMappings = ImmutableSet
            .<BlockDeviceMapping> builder();
      protected byte[] userData;

      public void clear() {
         groupIds = ImmutableSet.<String> builder();
         imageId = null;
         instanceType = null;
         kernelId = null;
         keyName = null;
         availabilityZone = null;
         ramdiskId = null;
         monitoringEnabled = false;
         blockDeviceMappings = ImmutableSet.<BlockDeviceMapping> builder();
         userData = null;
      }

      public Builder groupIds(Iterable<String> groupIds) {
         this.groupIds.addAll(checkNotNull(groupIds, "groupIds"));
         return this;
      }

      public Builder groupId(String groupId) {
         if (groupId != null)
            this.groupIds.add(groupId);
         return this;
      }

      public Builder imageId(String imageId) {
         this.imageId = imageId;
         return this;
      }

      public Builder monitoringEnabled(Boolean monitoringEnabled) {
         this.monitoringEnabled = monitoringEnabled;
         return this;
      }

      public Builder instanceType(String instanceType) {
         this.instanceType = instanceType;
         return this;
      }

      public Builder kernelId(String kernelId) {
         this.kernelId = kernelId;
         return this;
      }

      public Builder keyName(String keyName) {
         this.keyName = keyName;
         return this;
      }

      public Builder availabilityZone(String availabilityZone) {
         this.availabilityZone = availabilityZone;
         return this;
      }

      public Builder ramdiskId(String ramdiskId) {
         this.ramdiskId = ramdiskId;
         return this;
      }

      public Builder mapEBSSnapshotToDevice(String deviceName, String snapshotId, @Nullable Integer sizeInGib,
            boolean deleteOnTermination) {
         blockDeviceMappings.add(new MapEBSSnapshotToDevice(deviceName, snapshotId, sizeInGib, deleteOnTermination));
         return this;
      }

      public Builder mapNewVolumeToDevice(String deviceName, int sizeInGib, boolean deleteOnTermination) {
         blockDeviceMappings.add(new MapNewVolumeToDevice(deviceName, sizeInGib, deleteOnTermination));
         return this;
      }

      public Builder mapEphemeralDeviceToDevice(String deviceName, String virtualName) {
         blockDeviceMappings.add(new MapEphemeralDeviceToDevice(deviceName, virtualName));
         return this;
      }

      public Builder blockDeviceMapping(BlockDeviceMapping blockDeviceMapping) {
         this.blockDeviceMappings.add(checkNotNull(blockDeviceMapping, "blockDeviceMapping"));
         return this;
      }

      public Builder blockDeviceMappings(Iterable<? extends BlockDeviceMapping> blockDeviceMappings) {
         this.blockDeviceMappings.addAll(checkNotNull(blockDeviceMappings, "blockDeviceMappings"));
         return this;
      }

      public Builder userData(byte[] userData) {
         this.userData = userData;
         return this;
      }

      public LaunchSpecification build() {
         return new LaunchSpecification(instanceType, imageId, kernelId, ramdiskId, availabilityZone, keyName,
               groupIds.build(), blockDeviceMappings.build(), monitoringEnabled, userData);
      }

      public static Builder fromLaunchSpecification(LaunchSpecification in) {
         return new Builder().instanceType(in.getInstanceType()).imageId(in.getImageId()).kernelId(in.getKernelId())
               .ramdiskId(in.getRamdiskId()).availabilityZone(in.getAvailabilityZone()).keyName(in.getKeyName())
               .groupIds(in.getGroupIds()).blockDeviceMappings(in.getBlockDeviceMappings())
               .monitoringEnabled(in.isMonitoringEnabled()).userData(in.getUserData());
      }
   }

   protected final String instanceType;
   protected final String imageId;
   protected final String kernelId;
   protected final String ramdiskId;
   protected final String availabilityZone;
   protected final String keyName;
   protected final Set<String> groupIds;
   protected final Set<? extends BlockDeviceMapping> blockDeviceMappings;
   protected final Boolean monitoringEnabled;
   protected final byte[] userData;

   public LaunchSpecification(String instanceType, String imageId, String kernelId, String ramdiskId,
         String availabilityZone, String keyName, Iterable<String> groupIds,
         Iterable<? extends BlockDeviceMapping> blockDeviceMappings, Boolean monitoringEnabled, byte[] userData) {
      this.instanceType = checkNotNull(instanceType, "instanceType");
      this.imageId = checkNotNull(imageId, "imageId");
      this.kernelId = kernelId;
      this.ramdiskId = ramdiskId;
      this.availabilityZone = availabilityZone;
      this.keyName = keyName;
      this.groupIds = ImmutableSortedSet.copyOf(checkNotNull(groupIds, "groupIds"));
      this.blockDeviceMappings = ImmutableSortedSet.copyOf(checkNotNull(blockDeviceMappings, "blockDeviceMappings"));
      this.monitoringEnabled = monitoringEnabled;
      this.userData = userData;
   }

   /**
    * Image ID of the AMI used to launch the instance.
    */
   public String getImageId() {
      return imageId;
   }

   /**
    * CloudWatch support
    */
   public Boolean isMonitoringEnabled() {
      return monitoringEnabled;
   }

   /**
    * The instance type.
    */
   public String getInstanceType() {
      return instanceType;
   }

   /**
    * Optional. Kernel associated with this instance.
    */
   public String getKernelId() {
      return kernelId;
   }

   /**
    * If this instance was launched with an associated key pair, this displays the key pair name.
    */
   public String getKeyName() {
      return keyName;
   }

   /**
    * The location where the instance launched.
    */
   public String getAvailabilityZone() {
      return availabilityZone;
   }

   /**
    * Optional. RAM disk associated with this instance.
    */
   public String getRamdiskId() {
      return ramdiskId;
   }

   /**
    * volumes mappings associated with the instance.
    */
   public Set<? extends BlockDeviceMapping> getBlockDeviceMappings() {
      return blockDeviceMappings;
   }

   /**
    * Names of the security groups.
    */
   public Set<String> getGroupIds() {
      return groupIds;
   }

   /**
    * User Data
    */
   public byte[] getUserData() {
      return userData;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((availabilityZone == null) ? 0 : availabilityZone.hashCode());
      result = prime * result + ((blockDeviceMappings == null) ? 0 : blockDeviceMappings.hashCode());
      result = prime * result + ((groupIds == null) ? 0 : groupIds.hashCode());
      result = prime * result + ((imageId == null) ? 0 : imageId.hashCode());
      result = prime * result + ((instanceType == null) ? 0 : instanceType.hashCode());
      result = prime * result + ((kernelId == null) ? 0 : kernelId.hashCode());
      result = prime * result + ((keyName == null) ? 0 : keyName.hashCode());
      result = prime * result + ((monitoringEnabled == null) ? 0 : monitoringEnabled.hashCode());
      result = prime * result + ((ramdiskId == null) ? 0 : ramdiskId.hashCode());
      result = prime * result + Arrays.hashCode(userData);
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      LaunchSpecification other = (LaunchSpecification) obj;
      if (availabilityZone == null) {
         if (other.availabilityZone != null)
            return false;
      } else if (!availabilityZone.equals(other.availabilityZone))
         return false;
      if (blockDeviceMappings == null) {
         if (other.blockDeviceMappings != null)
            return false;
      } else if (!blockDeviceMappings.equals(other.blockDeviceMappings))
         return false;
      if (groupIds == null) {
         if (other.groupIds != null)
            return false;
      } else if (!groupIds.equals(other.groupIds))
         return false;
      if (imageId == null) {
         if (other.imageId != null)
            return false;
      } else if (!imageId.equals(other.imageId))
         return false;
      if (instanceType == null) {
         if (other.instanceType != null)
            return false;
      } else if (!instanceType.equals(other.instanceType))
         return false;
      if (kernelId == null) {
         if (other.kernelId != null)
            return false;
      } else if (!kernelId.equals(other.kernelId))
         return false;
      if (keyName == null) {
         if (other.keyName != null)
            return false;
      } else if (!keyName.equals(other.keyName))
         return false;
      if (monitoringEnabled == null) {
         if (other.monitoringEnabled != null)
            return false;
      } else if (!monitoringEnabled.equals(other.monitoringEnabled))
         return false;
      if (ramdiskId == null) {
         if (other.ramdiskId != null)
            return false;
      } else if (!ramdiskId.equals(other.ramdiskId))
         return false;
      if (!Arrays.equals(userData, other.userData))
         return false;
      return true;
   }

   public Builder toBuilder() {
      return Builder.fromLaunchSpecification(this);
   }

   @Override
   public String toString() {
      return "[instanceType=" + instanceType + ", imageId=" + imageId + ", kernelId=" + kernelId + ", ramdiskId="
            + ramdiskId + ", availabilityZone=" + availabilityZone + ", keyName=" + keyName + ", groupIds=" + groupIds
            + ", blockDeviceMappings=" + blockDeviceMappings + ", monitoringEnabled=" + monitoringEnabled
            + ", userData=" + (userData != null) + "]";
   }

}
