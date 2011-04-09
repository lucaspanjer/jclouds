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
package org.jclouds.savvis.vpdc.xml;

import static org.jclouds.util.SaxUtils.currentOrNull;
import static org.jclouds.util.SaxUtils.equalsOrSuffix;

import java.util.Map;

import javax.inject.Inject;
import javax.inject.Provider;

import org.jclouds.ovf.Network;
import org.jclouds.ovf.NetworkSection;
import org.jclouds.ovf.xml.SectionHandler;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;

/**
 * @author Adrian Cole
 */
public class NetworkSectionHandler extends SectionHandler<NetworkSection, NetworkSection.Builder> {
   protected Network.Builder networkBuilder = Network.builder();

   @Inject
   public NetworkSectionHandler(Provider<NetworkSection.Builder> builderProvider) {
      super(builderProvider);
   }

   public void startElement(String uri, String localName, String qName, Attributes attrs) {
      Map<String, String> attributes = SaxUtils.cleanseAttributes(attrs);
      if (equalsOrSuffix(qName, "Network")) {
         networkBuilder.name(attributes.get("name"));
      }
   }

   @Override
   public void endElement(String uri, String localName, String qName) {
      if (equalsOrSuffix(qName, "Info")) {
         builder.info(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Description")) {
         networkBuilder.description(currentOrNull(currentText));
      } else if (equalsOrSuffix(qName, "Network")) {
         try {
            builder.network(networkBuilder.build());
         } finally {
            networkBuilder = Network.builder();
         }
      }
      super.endElement(uri, localName, qName);
   }
}
