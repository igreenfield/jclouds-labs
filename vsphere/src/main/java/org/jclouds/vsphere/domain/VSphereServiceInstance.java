/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
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

package org.jclouds.vsphere.domain;

import com.vmware.vim25.mo.ServiceInstance;

import java.io.Closeable;
import java.io.IOException;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper class on the ServiceInstance from vSphere SDK
 * <p/>
 * Date: 3/5/14
 * Time: 10:26 AM
 */
public class VSphereServiceInstance implements Closeable {
   private ServiceInstance instance;

   public VSphereServiceInstance(ServiceInstance instance) {
      this.instance = checkNotNull(instance, "ServiceInstance");
   }

   public ServiceInstance getInstance() {
      return instance;
   }

   @Override
   public void close() throws IOException {
      instance.getServerConnection().logout();
   }
}