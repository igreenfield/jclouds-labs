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

import java.util.UUID;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;

/**
 * Dataset is a pre-built image ready to be cloned.
 */
public class DataSet {

   public static Builder builder() {
      return new Builder();
   }

   public Builder toBuilder() {
      return builder().fromDataSet(this);
   }

   public static class Builder {

      private UUID uuid;
      private String os;
      private String published;
      private String urn;

      public Builder uuid(UUID uuid) {
         this.uuid = uuid;
         return this;
      }

      public Builder uuid(String uuid) {
         this.uuid = UUID.fromString(uuid);
         return this;
      }

      public Builder os(String os) {
         this.os = os;
         return this;
      }

      public Builder published(String published) {
         this.published = published;
         return this;
      }

      public Builder urn(String urn) {
         this.urn = urn;
         return this;
      }

      public Builder fromDsadmString(String string) {
         String[] sections = string.split(" ");

         uuid(sections[0]);
         os(sections[1]);
         published(sections[2]);
         urn(sections[3]);

         return this;
      }

      public DataSet build() {
         return new DataSet(uuid, os, published, urn);
      }

      public Builder fromDataSet(DataSet in) {
         return uuid(in.getUuid()).os(in.getOs()).published(in.getPublished()).urn(in.getUrn());
      }
   }

   private final UUID uuid;
   private final String os;
   private final String published;
   private final String urn;

   protected DataSet(UUID uuid, String os, String published, String urn) {
      this.uuid = uuid;
      this.os = os;
      this.published = published;
      this.urn = urn;
   }

   public UUID getUuid() {
      return uuid;
   }

   public String getOs() {
      return os;
   }

   public String getPublished() {
      return published;
   }

   public String getUrn() {
      return urn;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public int hashCode() {
      // UUID is primary key
      return uuid.hashCode();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public boolean equals(Object obj) {
      if (this == obj)
         return true;
      if (obj == null)
         return false;
      if (getClass() != obj.getClass())
         return false;
      return uuid.equals(((DataSet) obj).uuid);
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public String toString() {
      return MoreObjects.toStringHelper(this).omitNullValues().add("uuid", uuid).add("os", os).add("published", published)
               .add("urn", urn).toString();
   }

}
