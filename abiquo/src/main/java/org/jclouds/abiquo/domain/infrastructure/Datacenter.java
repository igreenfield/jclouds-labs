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
package org.jclouds.abiquo.domain.infrastructure;

import static com.google.common.collect.Iterables.find;
import static com.google.common.collect.Iterables.transform;

import org.jclouds.abiquo.AbiquoApi;
import org.jclouds.abiquo.domain.DomainWrapper;
import org.jclouds.abiquo.domain.cloud.VirtualMachineTemplate;
import org.jclouds.abiquo.domain.enterprise.Enterprise;
import org.jclouds.abiquo.domain.enterprise.Limits;
import org.jclouds.abiquo.domain.infrastructure.options.DatacenterOptions;
import org.jclouds.abiquo.domain.infrastructure.options.IpmiOptions;
import org.jclouds.abiquo.domain.infrastructure.options.MachineOptions;
import org.jclouds.abiquo.domain.network.Network;
import org.jclouds.abiquo.domain.network.NetworkServiceType;
import org.jclouds.abiquo.domain.network.options.NetworkOptions;
import org.jclouds.abiquo.predicates.NetworkServiceTypePredicates;
import org.jclouds.collect.PagedIterable;
import org.jclouds.rest.ApiContext;

import com.abiquo.model.enumerator.HypervisorType;
import com.abiquo.model.enumerator.MachineIpmiState;
import com.abiquo.model.enumerator.MachineState;
import com.abiquo.model.enumerator.NetworkType;
import com.abiquo.model.enumerator.RemoteServiceType;
import com.abiquo.model.enumerator.VlanTagAvailabilityType;
import com.abiquo.server.core.appslibrary.VirtualMachineTemplateDto;
import com.abiquo.server.core.cloud.HypervisorTypeDto;
import com.abiquo.server.core.cloud.HypervisorTypesDto;
import com.abiquo.server.core.enterprise.DatacentersLimitsDto;
import com.abiquo.server.core.infrastructure.DatacenterDto;
import com.abiquo.server.core.infrastructure.MachineDto;
import com.abiquo.server.core.infrastructure.MachineIpmiStateDto;
import com.abiquo.server.core.infrastructure.MachineStateDto;
import com.abiquo.server.core.infrastructure.MachinesDto;
import com.abiquo.server.core.infrastructure.RackDto;
import com.abiquo.server.core.infrastructure.RacksDto;
import com.abiquo.server.core.infrastructure.RemoteServicesDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypeDto;
import com.abiquo.server.core.infrastructure.network.NetworkServiceTypesDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworkDto;
import com.abiquo.server.core.infrastructure.network.VLANNetworksDto;
import com.abiquo.server.core.infrastructure.network.VlanTagAvailabilityDto;
import com.abiquo.server.core.infrastructure.storage.StorageDeviceDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesDto;
import com.abiquo.server.core.infrastructure.storage.StorageDevicesMetadataDto;
import com.abiquo.server.core.infrastructure.storage.TiersDto;
import com.google.common.base.Function;

/**
 * Adds high level functionality to {@link DatacenterDto}.
 * 
 * @see API: <a
 *      href="http://community.abiquo.com/display/ABI20/DatacenterResource">
 *      http://community.abiquo.com/display/ABI20/DatacenterResource</a>
 */
public class Datacenter extends DomainWrapper<DatacenterDto> {
   /**
    * IP address of the datacenter (used to create all remote services with the
    * same ip).
    */
   private String ip;

   /**
    * Constructor to be used only by the builder.
    */
   protected Datacenter(final ApiContext<AbiquoApi> context, final DatacenterDto target) {
      super(context, target);
   }

   // Domain operations

   /**
    * Delete the datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-DeleteanexistingDatacenter"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- DeleteanexistingDatacenter</a>
    */
   public void delete() {
      context.getApi().getInfrastructureApi().deleteDatacenter(target);
      target = null;
   }

   /**
    * Create a datacenter in Abiquo. This method will perform several calls to
    * the API if remote services have been defined in the builder. Different
    * remote services will be created depending on the {@link AbiquoEdition}.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-CreateanewDatacenter"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- CreateanewDatacenter</a>
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RemoteServiceResource#RemoteServiceResource-CreateaRemoteService"
    *      > http://community.abiquo.com/display/ABI20/RemoteServiceResource#
    *      RemoteServiceResource- CreateaRemoteService</a>
    */
   public void save() {
      // Datacenter must be persisted first, so links get populated in the
      // target object
      target = context.getApi().getInfrastructureApi().createDatacenter(target);

      // If remote services data is set, create remote services.
      if (ip != null) {
         createRemoteServices();
      }
   }

   /**
    * Update datacenter information in the server with the data from this
    * datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Updateanexistingdatacenter"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Updateanexistingdatacenter </a>
    */
   public void update() {
      target = context.getApi().getInfrastructureApi().updateDatacenter(target);
   }

   /**
    * The cloud administrator will find it useful to know if a VLAN Tag is
    * already assigned before creating a new Public or External Network. This
    * method provides this functionality: Check if a tag is available inside the
    * Datacenter. Please refer link for more information.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/PublicNetworkResource#PublicNetworkResource-Checkthetagavailability"
    *      > http://community.abiquo.com/display/ABI20/PublicNetworkResource#
    *      PublicNetworkResource- Checkthetagavailability</a>
    * @return Availability state of the tag.
    */
   public VlanTagAvailabilityType checkTagAvailability(final int tag) {
      VlanTagAvailabilityDto availability = context.getApi().getInfrastructureApi().checkTagAvailability(target, tag);

      return availability.getAvailable();
   }

   // Children access

   /**
    * Retrieve the list of unmanaged racks in this datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrievealistofRacks"
    *      > http://community.abiquo.com/display/ABI20/RackResource#RackResource
    *      - RetrievealistofRacks</a>
    * @return List of unmanaged racks in this datacenter.
    */
   public Iterable<Rack> listRacks() {
      RacksDto racks = context.getApi().getInfrastructureApi().listRacks(target);
      return wrap(context, Rack.class, racks.getCollection());
   }

   /**
    * Retrieve a single unmanaged rack.
    * 
    * @param id
    *           Unique ID of the rack in this datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RackResource#RackResource-RetrieveaRack"
    *      >
    *      http://community.abiquo.com/display/ABI20/RackResource#RackResource-
    *      RetrieveaRack</a>
    * @return Unmanaged rack with the given id or <code>null</code> if it does
    *         not exist.
    */
   public Rack getRack(final Integer id) {
      RackDto rack = context.getApi().getInfrastructureApi().getRack(target, id);
      return wrap(context, Rack.class, rack);
   }

   /**
    * Retrieve the list of supported storage devices.
    * <p>
    * This method will get the list of the storage devices that are supported in
    * the datacenter.
    * 
    * @return List of supported storage devices. This list has only the default
    *         information for the storage devices, such as the management and
    *         iscsi ports, or the default credentials to access the device.
    */
   public Iterable<StorageDeviceMetadata> listSupportedStorageDevices() {
      StorageDevicesMetadataDto devices = context.getApi().getInfrastructureApi().listSupportedStorageDevices(target);
      return wrap(context, StorageDeviceMetadata.class, devices.getCollection());
   }

   /**
    * Retrieve the list of storage devices in this datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StorageDeviceResource#StorageDeviceResource-Retrievethelistofstoragedevices"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      StorageDeviceResource- Retrievethelistofstoragedevices</a>
    * @return List of storage devices in this datacenter.
    */
   public Iterable<StorageDevice> listStorageDevices() {
      StorageDevicesDto devices = context.getApi().getInfrastructureApi().listStorageDevices(target);
      return wrap(context, StorageDevice.class, devices.getCollection());
   }

   /**
    * Retrieve a single storage device.
    * 
    * @param id
    *           Unique ID of the storage device in this datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/StorageDeviceResource#StorageDeviceResource-Retrieveastoragedevice"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      StorageDeviceResource- Retrieveastoragedevice</a>
    * @return Storage device with the given id or <code>null</code> if it does
    *         not exist.
    */
   public StorageDevice getStorageDevice(final Integer id) {
      StorageDeviceDto device = context.getApi().getInfrastructureApi().getStorageDevice(target, id);
      return wrap(context, StorageDevice.class, device);
   }

   /**
    * Return the list of Network Service Types defined in a datacenter. By
    * default, a Network Service Type called 'Service Network' will be created
    * with the datacenter.
    * 
    * @return List of network services in this datacenter.
    */
   public Iterable<NetworkServiceType> listNetworkServiceTypes() {
      NetworkServiceTypesDto dtos = context.getApi().getInfrastructureApi().listNetworkServiceTypes(target);
      return wrap(context, NetworkServiceType.class, dtos.getCollection());
   }

   /**
    * Retrieve a single network service type.
    * 
    * @param id
    *           Unique ID of the network service type in this datacenter.
    * @return Network Service Type with the given id or <code>null</code> if it
    *         does not exist.
    */
   public NetworkServiceType getNetworkServiceType(final Integer id) {
      NetworkServiceTypeDto nst = context.getApi().getInfrastructureApi().getNetworkServiceType(target, id);
      return wrap(context, NetworkServiceType.class, nst);
   }

   /**
    * Return the default network service type used by the datacenter. This
    * datacenter will be the one used by {@link PrivateNetwork}. Even it can not
    * be deleted, it can be modified.
    * 
    * @return the defult {@link NetworkServiceType}
    */
   public NetworkServiceType defaultNetworkServiceType() {
      return find(listNetworkServiceTypes(), NetworkServiceTypePredicates.isDefault());
   }

   /**
    * Retrieve the list of remote services of this datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/RemoteServiceResource#RemoteServiceResource-RetrievealistofRemoteServices"
    *      > http://community.abiquo.com/display/ABI20/RemoteServiceResource#
    *      RemoteServiceResource- RetrievealistofRemoteServices</a>
    * @return List of remote services in this datacenter.
    */
   public Iterable<RemoteService> listRemoteServices() {
      RemoteServicesDto remoteServices = context.getApi().getInfrastructureApi().listRemoteServices(target);
      return wrap(context, RemoteService.class, remoteServices.getCollection());
   }

   private void createRemoteServices() {
      createRemoteService(RemoteServiceType.BPM_SERVICE);
      createRemoteService(RemoteServiceType.DHCP_SERVICE);
      createRemoteService(RemoteServiceType.STORAGE_SYSTEM_MONITOR);
      createRemoteService(RemoteServiceType.APPLIANCE_MANAGER);
      createRemoteService(RemoteServiceType.VIRTUAL_SYSTEM_MONITOR);
      createRemoteService(RemoteServiceType.NODE_COLLECTOR);
      createRemoteService(RemoteServiceType.VIRTUAL_FACTORY);
   }

   private void createRemoteService(final RemoteServiceType type) {
      RemoteService.builder(context, this).type(type).ip(this.ip).build().save();
   }

   /**
    * Retrieve the list of datacenter limits by all enterprises. The Datacenter
    * Limits resource allows you to assign datacenters and allocated resources
    * in them to be used by an enterprise.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievelimitsbydatacenter"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievelimitsbydatacenter</a>
    * @return List of datacenter limits by all enterprises.
    */
   public Iterable<Limits> listLimits() {
      DatacentersLimitsDto dto = context.getApi().getInfrastructureApi().listLimits(this.unwrap());
      return DomainWrapper.wrap(context, Limits.class, dto.getCollection());
   }

   /**
    * Retrieve the list of tiers in ths datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/TierResource#TierResource-Retrievethelistoftiers"
    *      >
    *      http://community.abiquo.com/display/ABI20/TierResource#TierResource-
    *      Retrievethelistoftiers </a>
    * @return List of tiers in this datacenter.
    */
   public Iterable<Tier> listTiers() {
      TiersDto dto = context.getApi().getInfrastructureApi().listTiers(this.unwrap());
      return DomainWrapper.wrap(context, Tier.class, dto.getCollection());
   }

   /**
    * Retrieve the list of public, external and unmanaged networks in this
    * datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/PublicNetworkResource#PublicNetworkResource-Getthelistofpublicnetworks"
    *      > http://community.abiquo.com/display/ABI20/PublicNetworkResource#
    *      PublicNetworkResource- Getthelistofpublicnetworks</a>
    * @return List of public, external and unmanaged networks in this
    *         datacenter.
    */
   public Iterable<Network<?>> listNetworks() {
      VLANNetworksDto networks = context.getApi().getInfrastructureApi().listNetworks(target);
      return Network.wrapNetworks(context, networks.getCollection());
   }

   /**
    * Retrieve the list of networks of this datacenter matching the given type.
    * 
    * @param type
    *           Network type filter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/PublicNetworkResource#PublicNetworkResource-Getthelistofpublicnetworks"
    *      > http://community.abiquo.com/display/ABI20/PublicNetworkResource#
    *      PublicNetworkResource- Getthelistofpublicnetworks</a>
    * @return List of networks of this datacenter matching the given type.
    */
   public Iterable<Network<?>> listNetworks(final NetworkType type) {
      NetworkOptions options = NetworkOptions.builder().type(type).build();
      VLANNetworksDto networks = context.getApi().getInfrastructureApi().listNetworks(target, options);
      return Network.wrapNetworks(context, networks.getCollection());
   }

   /**
    * Retrieve a single public, external or unmanaged network from this
    * datacenter.
    * {@link org.jclouds.abiquo.domain.network.Network#toExternalNetwork},
    * {@link org.jclouds.abiquo.domain.network.Network#toPublicNetwork} and
    * {@link org.jclouds.abiquo.domain.network.Network#toUnmanagedNetwork} can
    * be used to convert the Network into the appropriate domain object.
    * 
    * @param id
    *           Unique ID of the network in this datacenter.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/PublicNetworkResource#PublicNetworkResource-Getthelistofpublicnetworks"
    *      > http://community.abiquo.com/display/ABI20/StorageDeviceResource#
    *      PublicNetworkResource#
    *      PublicNetworkResource-Getthelistofpublicnetworks</a>
    * @return Network with the given id or <code>null</code> if it does not
    *         exist.
    */
   public Network<?> getNetwork(final Integer id) {
      VLANNetworkDto network = context.getApi().getInfrastructureApi().getNetwork(target, id);
      return Network.wrapNetwork(context, network);
   }

   // Actions

   /**
    * Retrieve the hypervisor type from remote machine.
    * 
    * @param ip
    *           IP address of the physical machine.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievethehypervisortypefromremotemachine"
    *      http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievethehypervisortypefromremotemachine</a>
    * @return Hypervisor type of the remote machine.
    * @throws Exception
    *            If the hypervisor type information cannot be retrieved.
    */
   public HypervisorType getHypervisorType(final String ip) {
      DatacenterOptions options = DatacenterOptions.builder().ip(ip).build();

      String type = context.getApi().getInfrastructureApi().getHypervisorTypeFromMachine(target, options);

      return HypervisorType.valueOf(type);
   }

   /**
    * Retrieve the list of available hypervisor types in the datacenter.
    * 
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveavailablehypervisortypes"
    *      http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveavailablehypervisortypes</a>
    * @return List of available hypervisor types in the datacenter.
    */
   public Iterable<HypervisorType> listAvailableHypervisors() {
      HypervisorTypesDto types = context.getApi().getInfrastructureApi().getHypervisorTypes(target);
      return getHypervisorTypes(types);
   }

   private Iterable<HypervisorType> getHypervisorTypes(final HypervisorTypesDto dtos) {
      return transform(dtos.getCollection(), new Function<HypervisorTypeDto, HypervisorType>() {
         @Override
         public HypervisorType apply(HypervisorTypeDto input) {
            return HypervisorType.fromId(input.getId());
         }
      });
   }

   /**
    * Searches a remote machine and retrieves an Machine object with its
    * information.
    * 
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return A physical machine if found or <code>null</code>.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    */
   public Machine discoverSingleMachine(final String ip, final HypervisorType hypervisorType, final String user,
         final String password) {
      return discoverSingleMachine(ip, hypervisorType, user, password, hypervisorType.defaultPort);
   }

   /**
    * Searches a remote machine and retrieves an Machine object with its
    * information.
    * 
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param port
    *           Port to connect.
    * @return A physical machine if found or <code>null</code>.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrieveremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrieveremotemachineinformation</a>
    */
   public Machine discoverSingleMachine(final String ip, final HypervisorType hypervisorType, final String user,
         final String password, final int port) {
      MachineDto dto = context
            .getApi()
            .getInfrastructureApi()
            .discoverSingleMachine(target, ip, hypervisorType, user, password,
                  MachineOptions.builder().port(port).build());

      // Credentials are not returned by the API
      dto.setUser(user);
      dto.setPassword(password);

      return wrap(context, Machine.class, dto);
   }

   /**
    * Searches multiple remote machines and retrieves an Machine list with its
    * information.
    * 
    * @param ipFrom
    *           IP address of the remote first hypervisor to check.
    * @param ipTo
    *           IP address of the remote last hypervisor to check.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievealistofremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievealistofremotemachineinformation</a>
    */
   public Iterable<Machine> discoverMultipleMachines(final String ipFrom, final String ipTo,
         final HypervisorType hypervisorType, final String user, final String password) {
      return discoverMultipleMachines(ipFrom, ipTo, hypervisorType, user, password, hypervisorType.defaultPort);
   }

   /**
    * Searches multiple remote machines and retrieves an Machine list with its
    * information.
    * 
    * @param ipFrom
    *           IP address of the remote first hypervisor to check.
    * @param ipTo
    *           IP address of the remote last hypervisor to check.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param port
    *           Port to connect.
    * @return The physical machine list.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Retrievealistofremotemachineinformation"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Retrievealistofremotemachineinformation</a>
    */
   public Iterable<Machine> discoverMultipleMachines(final String ipFrom, final String ipTo,
         final HypervisorType hypervisorType, final String user, final String password, final int port) {
      MachinesDto dto = context
            .getApi()
            .getInfrastructureApi()
            .discoverMultipleMachines(target, ipFrom, ipTo, hypervisorType, user, password,
                  MachineOptions.builder().port(port).build());

      // Credentials are not returned by the API
      for (MachineDto machine : dto.getCollection()) {
         machine.setUser(user);
         machine.setPassword(password);
      }

      return wrap(context, Machine.class, dto.getCollection());
   }

   /**
    * Check the state of a remote machine. This feature is used to check the
    * state from a remote machine giving its location, user, password and
    * hypervisor type. This machine does not need to be managed by Abiquo.
    * 
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine state if the machine is found or
    *         <code>null</code>.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Checkthestatefromremotemachine"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Checkthestatefromremotemachine</a>
    */
   public MachineState checkMachineState(final String ip, final HypervisorType hypervisorType, final String user,
         final String password) {
      return checkMachineState(ip, hypervisorType, user, password,
            MachineOptions.builder().port(hypervisorType.defaultPort).build());
   }

   /**
    * Check the state of a remote machine. This feature is used to check the
    * state from a remote machine giving its location, user, password and
    * hypervisor type. This machine does not need to be managed by Abiquo.
    * 
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param hypervisorType
    *           Kind of hypervisor we want to connect. Valid values are {vbox,
    *           kvm, xen-3, vmx-04, hyperv-301, xenserver}.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @param options
    *           .
    * @return The physical machine state if the machine is found or
    *         <code>null</code>.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Checkthestatefromremotemachine"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Checkthestatefromremotemachine</a>
    */
   public MachineState checkMachineState(final String ip, final HypervisorType hypervisorType, final String user,
         final String password, final MachineOptions options) {
      MachineStateDto dto = context.getApi().getInfrastructureApi()
            .checkMachineState(target, ip, hypervisorType, user, password, options);

      return dto.getState();
   }

   /**
    * Check the ipmi configuration state of a remote machine. This feature is
    * used to check the ipmi configuration state from a remote machine giving
    * its location, user and password. This machine does not need to be managed
    * by Abiquo.
    * 
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine state if the machine is found or
    *         <code>null</code>.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Checktheipmistatefromremotemachine"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Checktheipmistatefromremotemachine</a>
    */
   public MachineIpmiState checkMachineIpmiState(final String ip, final String user, final String password) {
      MachineIpmiStateDto dto = context.getApi().getInfrastructureApi()
            .checkMachineIpmiState(target, ip, user, password);
      return dto.getState();
   }

   /**
    * Check the ipmi configuration state of a remote machine. This feature is
    * used to check the ipmi configuration state from a remote machine giving
    * its location, user and password. This machine does not need to be managed
    * by Abiquo.
    * 
    * @param ip
    *           IP address of the remote hypervisor to connect.
    * @param user
    *           User to log in.
    * @param password
    *           Password to authenticate.
    * @return The physical machine state if the machine is found or
    *         <code>null</code>.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/DatacenterResource#DatacenterResource-Checktheipmistatefromremotemachine"
    *      > http://community.abiquo.com/display/ABI20/DatacenterResource#
    *      DatacenterResource- Checktheipmistatefromremotemachine</a>
    */
   public MachineIpmiState checkMachineIpmiState(final String ip, final String user, final String password,
         final IpmiOptions options) {
      MachineIpmiStateDto dto = context.getApi().getInfrastructureApi()
            .checkMachineIpmiState(target, ip, user, password, options);
      return dto.getState();
   }

   /**
    * Retrieve the list of virtual machine templates in the repository of this
    * datacenter.
    * 
    * @param enterprise
    *           Owner of the templates.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/VirtualMachineTemplateResource#VirtualMachineTemplateResource-Retrieveallvirtualmachinetemplates"
    *      > http://community.abiquo.com/display/ABI20/
    *      VirtualMachineTemplateResource#
    *      VirtualMachineTemplateResource-Retrieveallvirtualmachinetemplates</a>
    * @return List of virtual machine templates in the repository of this
    *         datacenter.
    */
   public Iterable<VirtualMachineTemplate> listTemplatesInRepository(final Enterprise enterprise) {
      PagedIterable<VirtualMachineTemplateDto> templates = context.getApi().getVirtualMachineTemplateApi()
            .listVirtualMachineTemplates(enterprise.getId(), target.getId());
      return wrap(context, VirtualMachineTemplate.class, templates.concat());
   }

   /**
    * Retrieve a single virtual machine template in of this datacenter from the
    * given enterprise.
    * 
    * @param enterprise
    *           Owner of the templates.
    * @param id
    *           Unique ID of the template in the datacenter repository for the
    *           given enterprise.
    * @see API: <a href=
    *      "http://community.abiquo.com/display/ABI20/VirtualMachineTemplateResource#VirtualMachineTemplateResource-Retrieveallvirtualmachinetemplates"
    *      > http://community.abiquo.com/display/ABI20/
    *      VirtualMachineTemplateResource#
    *      VirtualMachineTemplateResource-Retrieveallvirtualmachinetemplates</a>
    * @return Virtual machine template with the given id in the given enterprise
    *         or <code>null</code> if it does not exist.
    */
   public VirtualMachineTemplate getTemplateInRepository(final Enterprise enterprise, final Integer id) {
      VirtualMachineTemplateDto template = context.getApi().getVirtualMachineTemplateApi()
            .getVirtualMachineTemplate(enterprise.getId(), target.getId(), id);
      return wrap(context, VirtualMachineTemplate.class, template);
   }

   // Builder

   public static Builder builder(final ApiContext<AbiquoApi> context) {
      return new Builder(context);
   }

   public static class Builder {
      private ApiContext<AbiquoApi> context;

      private String name;

      private String location;

      private String ip;

      public Builder(final ApiContext<AbiquoApi> context) {
         super();
         this.context = context;
      }

      public Builder remoteServices(final String ip) {
         this.ip = ip;
         return this;
      }

      public Builder name(final String name) {
         this.name = name;
         return this;
      }

      public Builder location(final String location) {
         this.location = location;
         return this;
      }

      public Datacenter build() {
         DatacenterDto dto = new DatacenterDto();
         dto.setName(name);
         dto.setLocation(location);
         Datacenter datacenter = new Datacenter(context, dto);
         datacenter.ip = ip;
         return datacenter;
      }

      public static Builder fromDatacenter(final Datacenter in) {
         return Datacenter.builder(in.context).name(in.getName()).location(in.getLocation());
      }
   }

   // Delegate methods

   public Integer getId() {
      return target.getId();
   }

   public String getLocation() {
      return target.getLocation();
   }

   public String getName() {
      return target.getName();
   }

   public void setLocation(final String location) {
      target.setLocation(location);
   }

   public void setName(final String name) {
      target.setName(name);
   }

   public String getUUID() {
      return target.getUuid();
   }

   @Override
   public String toString() {
      return "Datacenter [id=" + getId() + ", location=" + getLocation() + ", name=" + getName() + ", uuid="
            + getUUID() + "]";
   }

}
