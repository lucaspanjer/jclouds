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
package org.jclouds.logging.slf4j.config;

import static org.testng.Assert.assertEquals;

import javax.annotation.Resource;

import org.jclouds.logging.Logger;
import org.jclouds.logging.slf4j.SLF4JLogger;
import org.testng.annotations.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

@Test
public class SLF4JLoggingModuleTest {

   static class A {
      @Resource
      Logger logger = Logger.NULL;
   }

   @Test
   public void testConfigure() {
      Injector i = Guice.createInjector(new SLF4JLoggingModule());
      A a = i.getInstance(A.class);
      assertEquals(a.logger.getClass(), SLF4JLogger.class);
      assertEquals(a.logger.getCategory(), getClass().getName() + "$A");
   }

}
