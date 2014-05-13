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
package org.jclouds.vsphere.predicates;

import com.google.common.base.Predicate;
import com.google.common.net.InetAddresses;
import com.vmware.vim25.VirtualMachineToolsStatus;
import com.vmware.vim25.mo.HostSystem;
import com.vmware.vim25.mo.ResourcePool;
import com.vmware.vim25.mo.VirtualMachine;

import java.net.Inet4Address;
import java.net.Inet6Address;
import java.rmi.RemoteException;

/**
 * @author Izek Greenfield
 */
public class VSpherePredicate {

    public static final Predicate<VirtualMachine> isTemplatePredicate = new Predicate<VirtualMachine>() {
        @Override
        public boolean apply(VirtualMachine virtualMachine) {
            try {
                return virtualMachine.getConfig().isTemplate();
            } catch (Exception e) {
                return true;
            }
        }

        @Override
        public String toString() {
            return "IsTemplatePredicate()";
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

    public static Predicate<ResourcePool> isResourcePoolOf(String hostname) {
        return new IsResourcePoolOf(hostname);
    }

    public static Predicate<VirtualMachine> IsToolsStatusEquals(VirtualMachineToolsStatus status) {
        return new IsToolsStatusEquals(status);
    }

    public static Predicate<VirtualMachine> hasMORid(String morId) {
        return new hasMORid(morId);
    }
}

class hasMORid implements Predicate<VirtualMachine> {
    private String morId = null;

    hasMORid(String morId) {
        this.morId = morId;
    }

    @Override
    public boolean apply(VirtualMachine input) {
        return input.getMOR().getVal().equals(morId);
    }

    @Override
    public String toString() {
        return "hasMORid";
    }
}

class IsResourcePoolOf implements Predicate<ResourcePool> {

    private String hostname;

    IsResourcePoolOf(String hostname) {
        this.hostname = hostname;
    }

    @Override
    public boolean apply(ResourcePool input) {
        try {
            for (HostSystem hostSystem : input.getOwner().getHosts()) {
                if (hostSystem.getName().equals(hostname))
                    return true;
            }
        } catch (RemoteException e) {
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "IsResourcePoolOf";
    }

}

class IsToolsStatusEquals implements Predicate<VirtualMachine> {

    private VirtualMachineToolsStatus status;

    IsToolsStatusEquals(VirtualMachineToolsStatus status) {
        this.status = status;
    }

    @Override
    public boolean apply(VirtualMachine input) {
        try {
            if (input.getGuest().getToolsStatus().equals(status))
                return true;
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    @Override
    public String toString() {
        return "IsToolsStatusEquals";
    }

}