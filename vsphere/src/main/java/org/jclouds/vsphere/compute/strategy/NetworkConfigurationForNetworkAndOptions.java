/*
* Copyright 2014 Cisco Systems, Inc
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*    http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.jclouds.vsphere.compute.strategy;

import org.jclouds.vsphere.compute.options.VSphereTemplateOptions;
import org.jclouds.vsphere.domain.network.NetworkConfig;

import javax.inject.Inject;
import java.util.Set;

/**
 * 
 * @author Izek Greenfield
 */
public class NetworkConfigurationForNetworkAndOptions {
   protected final NetworkConfig defaultNetworkConfig;

   @Inject
   protected NetworkConfigurationForNetworkAndOptions() {
      this.defaultNetworkConfig = new NetworkConfig("default");

   }

   /**
    * 
    * returns a {@link NetworkConfig} used to instantiate a vAppTemplate to
    * either the default parent (org) network, or one specified by options.
    * 
    * @param networkName
    *           network defined in the vSphere you wish to connect to
    * @param vOptions
    *           options to override defaults with
    * @return
    */
   public NetworkConfig apply(String networkName, VSphereTemplateOptions vOptions) {
      NetworkConfig config;
      String userDefinedAddressType = vOptions.getAddressType();
      Set<String> userDefinedNetworks = vOptions.getNetworks();
      //FenceMode fenceMode = vOptions.getFenceMode() != null ? vOptions.getFenceMode() : defaultFenceMode;
      if (userDefinedAddressType != null) {
         config = NetworkConfig.builder().networkName(networkName).addressType(userDefinedAddressType).build();
      } else {
         config = defaultNetworkConfig.toBuilder().networkName(networkName).addressType("generated").build();
      }

      return config;
   }

}
