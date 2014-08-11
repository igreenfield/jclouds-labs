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
package org.jclouds.azure.management.xml;

import java.util.Set;

import org.jclouds.azure.management.domain.Disk;
import org.jclouds.http.functions.ParseSax;
import org.jclouds.util.SaxUtils;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.google.inject.Inject;

public class ListDisksHandler extends ParseSax.HandlerForGeneratedRequestWithResult<Set<Disk>> {

   private final DiskHandler diskHandler;

   private Builder<Disk> disks = ImmutableSet.<Disk> builder();

   private boolean inDisk;

   @Inject
   public ListDisksHandler(final DiskHandler diskHandler) {
      this.diskHandler = diskHandler;
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public Set<Disk> getResult() {
      return disks.build();
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void startElement(String url, String name, String qName, Attributes attributes) throws SAXException {
      if (SaxUtils.equalsOrSuffix(qName, "Disk")) {
         inDisk = true;
      }
      if (inDisk) {
         diskHandler.startElement(url, name, qName, attributes);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void endElement(String uri, String name, String qName) throws SAXException {
      if (qName.equals("Disk")) {
         inDisk = false;
         disks.add(diskHandler.getResult());
      } else if (inDisk) {
         diskHandler.endElement(uri, name, qName);
      }
   }

   /**
    * {@inheritDoc}
    */
   @Override
   public void characters(char ch[], int start, int length) {
      if (inDisk) {
         diskHandler.characters(ch, start, length);
      }
   }

}
