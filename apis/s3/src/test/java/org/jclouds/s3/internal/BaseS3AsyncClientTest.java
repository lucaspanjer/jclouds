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
package org.jclouds.s3.internal;

import static org.testng.Assert.assertEquals;

import java.io.IOException;

import org.jclouds.http.HttpRequest;
import org.jclouds.rest.RestClientTest;
import org.jclouds.rest.RestContextFactory;
import org.jclouds.rest.RestContextSpec;
import org.jclouds.s3.S3AsyncClient;
import org.jclouds.s3.S3Client;
import org.jclouds.s3.S3ContextBuilder;
import org.jclouds.s3.S3PropertiesBuilder;
import org.jclouds.s3.blobstore.functions.BlobToObject;
import org.jclouds.s3.filters.RequestAuthorizeSignature;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Module;

/**
 * 
 * @author Adrian Cole
 */
@Test(groups = "unit")
public abstract class BaseS3AsyncClientTest<T extends S3AsyncClient> extends RestClientTest<T> {

   protected BlobToObject blobToS3Object;
   protected RequestAuthorizeSignature filter;

   @Override
   protected void checkFilters(HttpRequest request) {
      assertEquals(request.getFilters().size(), 1);
      assertEquals(request.getFilters().get(0).getClass(), RequestAuthorizeSignature.class);
   }


   @BeforeClass
   @Override
   protected void setupFactory() throws IOException {
      super.setupFactory();
      blobToS3Object = injector.getInstance(BlobToObject.class);
      filter = injector.getInstance(RequestAuthorizeSignature.class);
   }

   public BaseS3AsyncClientTest() {
      super();
   }

   protected String provider = "s3";
   protected String endpoint = "https://s3.amazonaws.com";

   /**
    * this is only here as "s3" is not in rest.properties
    */
   @SuppressWarnings({ "unchecked", "rawtypes" })
   @Override
   public RestContextSpec<?, ?> createContextSpec() {
      return RestContextFactory.<S3Client, S3AsyncClient> contextSpec(provider, endpoint,
            S3AsyncClient.VERSION, "", "", "identity", "credential", S3Client.class, S3AsyncClient.class,
            (Class) S3PropertiesBuilder.class, (Class) S3ContextBuilder.class, ImmutableSet.<Module> of());
   }

}
