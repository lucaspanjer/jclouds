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
package org.jclouds.blobstore.strategy;

import org.jclouds.blobstore.options.ListContainerOptions;
import org.jclouds.blobstore.strategy.internal.CountBlobTypeInList;

import com.google.inject.ImplementedBy;

/**
 * Number of objects in a container
 * 
 * @author Adrian Cole
 */
@ImplementedBy(CountBlobTypeInList.class)
public interface CountListStrategy {

   long execute(String containerName, ListContainerOptions options);

}