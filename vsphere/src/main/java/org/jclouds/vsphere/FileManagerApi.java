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

package org.jclouds.vsphere;

import com.vmware.vim25.FileFault;
import com.vmware.vim25.InvalidDatastore;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.UserNotFound;
import com.vmware.vim25.mo.Datacenter;
import com.vmware.vim25.mo.Task;

import java.io.IOException;
import java.rmi.RemoteException;

/**
 * Date: 18/05/2014 5:31 PM
 * Package: org.jclouds.vsphere
 */
public interface FileManagerApi {

   void uploadFile(String srcFilePath, String destDirectory) throws IOException;

   void changeOwner(java.lang.String name, Datacenter datacenter, java.lang.String owner) throws InvalidDatastore, FileFault, UserNotFound, RuntimeFault, RemoteException;

   Task copyDatastoreFile_Task(String sourceName, Datacenter sourceDatacenter, String destinationName, Datacenter destinationDatacenter, boolean force) throws FileFault, InvalidDatastore, RuntimeFault, RemoteException;

   Task deleteDatastoreFile_Task(String name, Datacenter datacenter) throws FileFault, InvalidDatastore, RuntimeFault, RemoteException;

   void makeDirectory(String name, Datacenter datacenter, boolean createParentDirectories) throws FileFault, InvalidDatastore, RuntimeFault, java.rmi.RemoteException;

   Task moveDatastoreFile_Task(String sourceName, Datacenter sourceDatacenter, String destinationName, Datacenter destinationDatacenter, boolean force) throws FileFault, InvalidDatastore, RuntimeFault, RemoteException;
}
