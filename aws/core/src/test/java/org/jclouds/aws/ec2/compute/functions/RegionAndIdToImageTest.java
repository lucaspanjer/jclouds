/**
 *
 * Copyright (C) 2009 Cloud Conscious, LLC. <info@cloudconscious.com>
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
package org.jclouds.aws.ec2.compute.functions;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.createNiceMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.jclouds.aws.ec2.options.DescribeImagesOptions.Builder.imageIds;
import static org.testng.Assert.assertEquals;

import java.util.Set;

import org.jclouds.aws.ec2.compute.domain.RegionAndName;
import org.jclouds.aws.ec2.services.AMIClient;
import org.jclouds.compute.domain.Image;
import org.jclouds.rest.ResourceNotFoundException;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;

/**
 * @author Adrian Cole
 */
@Test(groups = "unit", testName = "ec2.RegionAndIdToImageTest")
public class RegionAndIdToImageTest {

   @Test
   public void testApply() {

      ImageParser parser = createMock(ImageParser.class);
      AMIClient client = createMock(AMIClient.class);
      org.jclouds.aws.ec2.domain.Image ec2Image = createMock(org.jclouds.aws.ec2.domain.Image.class);
      Image image = createNiceMock(Image.class);
      Set<org.jclouds.aws.ec2.domain.Image> images = ImmutableSet
               .<org.jclouds.aws.ec2.domain.Image> of(ec2Image);
      
      expect(client.describeImagesInRegion("region", imageIds("ami"))).andReturn(images);
      expect(parser.apply(ec2Image)).andReturn(image);

      replay(image);
      replay(parser);
      replay(client);

      RegionAndIdToImage function = new RegionAndIdToImage(parser, client);

      assertEquals(function.apply(new RegionAndName("region", "ami")), image);

      verify(image);
      verify(parser);
      verify(client);

   }
   
   @Test
   public void testApplyNotFound() {

      ImageParser parser = createMock(ImageParser.class);
      AMIClient client = createMock(AMIClient.class);
      org.jclouds.aws.ec2.domain.Image ec2Image = createMock(org.jclouds.aws.ec2.domain.Image.class);
      Image image = createNiceMock(Image.class);
      Set<org.jclouds.aws.ec2.domain.Image> images = ImmutableSet
               .<org.jclouds.aws.ec2.domain.Image> of(ec2Image);
      
      expect(client.describeImagesInRegion("region", imageIds("ami"))).andReturn(images);
      expect(parser.apply(ec2Image)).andThrow(new ResourceNotFoundException());

      replay(image);
      replay(parser);
      replay(client);

      RegionAndIdToImage function = new RegionAndIdToImage(parser, client);

      assertEquals(function.apply(new RegionAndName("region", "ami")), null);

      verify(image);
      verify(parser);
      verify(client);

   }
}