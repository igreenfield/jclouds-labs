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

import com.vmware.vim25.mo.AlarmManager;
import com.vmware.vim25.mo.AuthorizationManager;
import com.vmware.vim25.mo.ClusterProfileManager;
import com.vmware.vim25.mo.CustomFieldsManager;
import com.vmware.vim25.mo.CustomizationSpecManager;
import com.vmware.vim25.mo.DiagnosticManager;
import com.vmware.vim25.mo.DistributedVirtualSwitchManager;
import com.vmware.vim25.mo.EventManager;
import com.vmware.vim25.mo.ExtensionManager;
import com.vmware.vim25.mo.Folder;
import com.vmware.vim25.mo.GuestOperationsManager;
import com.vmware.vim25.mo.HostLocalAccountManager;
import com.vmware.vim25.mo.HostProfileManager;
import com.vmware.vim25.mo.IpPoolManager;
import com.vmware.vim25.mo.LicenseManager;
import com.vmware.vim25.mo.LocalizationManager;
import com.vmware.vim25.mo.OptionManager;
import com.vmware.vim25.mo.PerformanceManager;
import com.vmware.vim25.mo.ProfileComplianceManager;
import com.vmware.vim25.mo.ScheduledTaskManager;
import com.vmware.vim25.mo.ServerConnection;
import com.vmware.vim25.mo.SessionManager;
import com.vmware.vim25.mo.TaskManager;
import com.vmware.vim25.mo.ViewManager;
import com.vmware.vim25.mo.VirtualDiskManager;
import org.jclouds.rest.annotations.Delegate;

import java.io.Closeable;

/**
 * Provides access to vSphere resources via their REST API.
 * <p/>
 *
 * @see <a href="https://communities.vmware.com/community/vmtn/developer/forums/vspherewebsdk" />
 * based on Andrea Turli work.
 */
public interface VSphereApi extends Closeable {
   @Delegate
   ClusterProfileManager getClusterProfileManagerApi();

   @Delegate
   AlarmManager getAlarmManagerApi();

   @Delegate
   AuthorizationManager getAuthorizationManagerApi();

   @Delegate
   CustomFieldsManager getCustomFieldsManagerApi();

   @Delegate
   CustomizationSpecManager getCustomizationSpecManagerApi();

   @Delegate
   EventManager getEventManagerApi();

   @Delegate
   DiagnosticManager getDiagnosticManagerApi();

   @Delegate
   DistributedVirtualSwitchManager getDistributedVirtualSwitchManagerApi();

   @Delegate
   ExtensionManager getExtensionManagerApi();

   @Delegate
   IFileManager getFileManagerApi();

   @Delegate
   GuestOperationsManager getGuestOperationsManagerApi();

   @Delegate
   HostLocalAccountManager getAccountManagerApi();

   @Delegate
   LicenseManager getLicenseManagerApi();

   @Delegate
   LocalizationManager getLocalizationManagerApi();

   @Delegate
   PerformanceManager getPerformanceManagerApi();

   @Delegate
   ProfileComplianceManager getProfileComplianceManagerApi();

   @Delegate
   ScheduledTaskManager getScheduledTaskManagerApi();

   @Delegate
   SessionManager getSessionManagerApi();

   @Delegate
   HostProfileManager getHostProfileManagerApi();

   @Delegate
   IpPoolManager getIpPoolManagerApi();

   @Delegate
   TaskManager getTaskManagerApi();

   @Delegate
   ViewManager getViewManagerApi();

   @Delegate
   VirtualDiskManager getVirtualDiskManagerApi();

   @Delegate
   OptionManager getOptionManagerApi();

   @Delegate
   Folder getRootFolder();

   @Delegate
   ServerConnection getServerConnection();

}
