/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jclouds.smartos.compute.domain;

import java.beans.ConstructorProperties;

import javax.inject.Named;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Specification of a network card.
 */
public class VmNIC {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromVmNIC(this);
   }

   public static class Builder {

      private String tag = "admin";
      private String ip;
      private String netmask;
      private String gateway;

      public Builder simpleDHCPNic() {
         tag = "admin";
         ip = "dhcp";
         return this;
      }

      public Builder tag(String tag) {
         this.tag = tag;
         return this;
      }

      public Builder ip(String ip) {
         this.ip = ip;
         return this;
      }

      public Builder netmask(String netmask) {
         this.netmask = netmask;
         return this;
      }

      public Builder gateway(String gateway) {
         this.gateway = gateway;
         return this;
      }

      public VmNIC build() {
         return new VmNIC(tag, ip, netmask, gateway);
      }

      public Builder fromVmNIC(VmNIC in) {
         return tag(in.getTag()).ip(in.getIp()).netmask(in.getNetmask()).gateway(in.getGateway());
      }
   }

   @Named("nic_tag")
   private final String tag;
   private final String ip;
   private final String netmask;
   private final String gateway;

   @ConstructorProperties({ "nic_tag", "ip", "netmask", "gateway" })
   protected VmNIC(String tag, String ip, String netmask, String gateway) {
      this.tag = tag;
      this.ip = ip;
      this.netmask = netmask;
      this.gateway = gateway;
   }

   public String getTag() {
      return tag;
   }

   public String getIp() {
      return ip;
   }

   public String getNetmask() {
      return netmask;
   }

   public String getGateway() {
      return gateway;
   }

   @Override
   public int hashCode() {
      return Objects.hashCode(tag, ip, netmask, gateway);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null || getClass() != obj.getClass())
         return false;
      VmNIC that = VmNIC.class.cast(obj);
      return Objects.equal(this.tag, that.tag) && Objects.equal(this.ip, that.ip)
               && Objects.equal(this.netmask, that.netmask) && Objects.equal(this.gateway, that.gateway);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).omitNullValues().add("tag", tag).add("ip", ip).add("netmask", netmask)
               .add("gateway", gateway).toString();
   }
}
