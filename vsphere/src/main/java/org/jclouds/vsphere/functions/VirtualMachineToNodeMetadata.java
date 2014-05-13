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

package org.jclouds.vsphere.functions;

import com.google.common.base.*;
import com.google.common.io.Closer;
import com.google.common.net.InetAddresses;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.vmware.vim25.*;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.VirtualMachine;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.NodeMetadata.Status;
import org.jclouds.compute.domain.NodeMetadataBuilder;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.domain.LocationBuilder;
import org.jclouds.domain.LocationScope;
import org.jclouds.logging.Logger;
import org.jclouds.util.InetAddresses2;
import org.jclouds.vsphere.config.VSphereConstants;
import org.jclouds.vsphere.domain.VSphereServiceInstance;
import org.jclouds.vsphere.predicates.VSpherePredicate;

import javax.annotation.Resource;
import javax.inject.Named;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.Inet6Address;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Set;

import static com.google.common.base.Predicates.not;
import static com.google.common.collect.Iterables.filter;
import static com.google.common.collect.Sets.newHashSet;

@Singleton
public class VirtualMachineToNodeMetadata implements Function<VirtualMachine, NodeMetadata> {

    public static final Splitter COMMA_SPLITTER = Splitter.on(",");
    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final Map<VirtualMachinePowerState, Status> toPortableNodeStatus;
    private final Supplier<Map<String, CustomFieldDef>> customFields;
    private final Supplier<VSphereServiceInstance> serviceInstanceSupplier;

    @Inject
    public VirtualMachineToNodeMetadata(Map<VirtualMachinePowerState, NodeMetadata.Status> toPortableNodeStatus,
                                        Supplier<Map<String, CustomFieldDef>> customFields,
                                        Supplier<VSphereServiceInstance> serviceInstanceSupplier) {
        this.toPortableNodeStatus = toPortableNodeStatus;
        this.customFields = customFields;
        this.serviceInstanceSupplier = serviceInstanceSupplier;
    }

    @Override
    public NodeMetadata apply(VirtualMachine vm) {
        Closer closer = Closer.create();
        VSphereServiceInstance instance = serviceInstanceSupplier.get();
        closer.register(instance);
        VirtualMachine freshVm = null;
        String virtualMachineName = "";
        NodeMetadataBuilder nodeMetadataBuilder = new NodeMetadataBuilder();
        try {
            try {
                String vmMORId = vm.getMOR().get_value();
                ManagedEntity[] vms = new InventoryNavigator(instance.getInstance().getRootFolder()).searchManagedEntities("VirtualMachine");
                for (ManagedEntity machine : vms) {

                    if (machine.getMOR().getVal().equals(vmMORId)) {
                        freshVm = (VirtualMachine) machine;
                        break;
                    }
                }
                LocationBuilder locationBuilder = new LocationBuilder();
                locationBuilder.description("");
                locationBuilder.id("");
                locationBuilder.scope(LocationScope.HOST);

                virtualMachineName = freshVm.getName();

                VirtualMachinePowerState vmState = freshVm.getRuntime().getPowerState();
                NodeMetadata.Status nodeState = toPortableNodeStatus.get(vmState);
                if (nodeState == null)
                    nodeState = Status.UNRECOGNIZED;



                nodeMetadataBuilder.name(virtualMachineName).ids(virtualMachineName)
                        .location(locationBuilder.build())
                        .hostname(virtualMachineName);

                String host = freshVm.getServerConnection().getUrl().getHost();

                try {
                    nodeMetadataBuilder.uri(new URI("https://" + host + ":9443/vsphere-client/vmrc/vmrc.jsp?vm=urn:vmomi:VirtualMachine:" + vmMORId + ":" + freshVm.getSummary().getConfig().getUuid()));
                } catch (URISyntaxException e) {
                }


                Set<String> ipv4Addresses = newHashSet();
                Set<String> ipv6Addresses = newHashSet();

                int retries = 0;
                while (nodeState == Status.RUNNING && !freshVm.getConfig().isTemplate() && Strings.isNullOrEmpty(freshVm.getGuest().getIpAddress()) && retries < 20) {
                    try {
                        Thread.sleep(10 * 1000);
                    } catch (InterruptedException e) {
                    }
                    retries++;
                }

                if (VSpherePredicate.IsToolsStatusEquals(VirtualMachineToolsStatus.toolsNotInstalled).apply(freshVm))
                    logger.debug("No VMware tools installed ( " + virtualMachineName + " )");
                else if (nodeState == Status.RUNNING && not(VSpherePredicate.isTemplatePredicate).apply(freshVm)) {
                    GuestNicInfo[] nics = freshVm.getGuest().getNet();
                    if (null != nics) {
                        for (GuestNicInfo nic : nics) {
                            String[] addresses = nic.getIpAddress();
                            if (null != addresses) {
                                for (String address : addresses) {
                                    if (isInet4Address.apply(address)) {
                                        ipv4Addresses.add(address);
                                    } else if (isInet6Address.apply(address)) {
                                        ipv6Addresses.add(address);
                                    }
                                }
                            }
                        }
                    }
                    nodeMetadataBuilder.publicAddresses(filter(ipv4Addresses, not(isPrivateAddress)));
                    nodeMetadataBuilder.privateAddresses(filter(ipv4Addresses, isPrivateAddress));
                }

                CustomFieldValue[] customFieldValues = freshVm.getCustomValue();
                if (customFieldValues != null) {
                    for (CustomFieldValue customFieldValue : customFieldValues) {
                        if (customFieldValue.getKey() == customFields.get().get(VSphereConstants.JCLOUDS_TAGS).getKey()) {
                            nodeMetadataBuilder.tags(COMMA_SPLITTER.split(((CustomFieldStringValue) customFieldValue).getValue()));
                        } else if (customFieldValue.getKey() == customFields.get().get(VSphereConstants.JCLOUDS_GROUP).getKey()) {
                            nodeMetadataBuilder.group(((CustomFieldStringValue) customFieldValue).getValue());
                        }
                    }
                }
                nodeMetadataBuilder.status(nodeState);
                return nodeMetadataBuilder.build();
            } catch (Throwable t) {
                logger.error("Got an exception for virtual machine name : " + virtualMachineName);
                Throwables.propagate(closer.rethrow(t));
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            return nodeMetadataBuilder.build();
        }
        return nodeMetadataBuilder.build();
    }

    Predicate<String> ipAddressTester = new Predicate<String>() {

        @Override
        public boolean apply(String input) {
            return !input.isEmpty();
        }

    };

    private static final Predicate<String> isPrivateAddress = new Predicate<String>() {
        public boolean apply(String in) {
            return InetAddresses2.IsPrivateIPAddress.INSTANCE.apply(in);
        }
    };

    public static final Predicate<String> isInet4Address = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            try {
                // Note we can do this, as InetAddress is now on the white list
                return InetAddresses.forString(input) instanceof Inet4Address;
            } catch (IllegalArgumentException e) {
                // could be a hostname
                return false;
            }
        }

    };
    public static final Predicate<String> isInet6Address = new Predicate<String>() {
        @Override
        public boolean apply(String input) {
            try {
                // Note we can do this, as InetAddress is now on the white list
                return InetAddresses.forString(input) instanceof Inet6Address;
            } catch (IllegalArgumentException e) {
                // could be a hostname
                return false;
            }
        }

    };
}