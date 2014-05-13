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
package org.jclouds.vsphere;

import com.vmware.vim25.mo.*;
import org.jclouds.rest.annotations.Delegate;

import java.io.Closeable;

/**
 * Provides access to vSphere resources via their REST API.
 * <p/>
 * 
 * @see <a href="https://communities.vmware.com/community/vmtn/developer/forums/vspherewebsdk" />
 * @author Izek Greenfield
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
    FileManager getFileManagerApi();
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
