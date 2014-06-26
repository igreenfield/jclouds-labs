package org.jclouds.vsphere;

import com.vmware.vim25.DuplicateName;
import com.vmware.vim25.ProfileCreateSpec;
import com.vmware.vim25.ProfilePolicyMetadata;
import com.vmware.vim25.RuntimeFault;
import com.vmware.vim25.mo.ManagedEntity;
import com.vmware.vim25.mo.Profile;

import java.rmi.RemoteException;
import java.util.List;

/**
 * CISCO LTD.
 * User: igreenfi
 * Date: 26/06/2014 10:23 AM
 * Package: org.jclouds.vsphere
 */
public interface ClusterProfileManagerApi {
    List<Profile> getProfile();

    List<Profile> createProfile(ProfileCreateSpec createSpec) throws DuplicateName, RuntimeFault, RemoteException;

    List<Profile> findAssociatedProfile(ManagedEntity entity) throws RuntimeFault, RemoteException;

    /**
     * SDK4.1 signature for back compatibility
     */
    List<ProfilePolicyMetadata> queryPolicyMetadata(List<String> policyName) throws RuntimeFault, RemoteException;

    /**
     * SDK5.0 signature
     */
    List<ProfilePolicyMetadata> queryPolicyMetadata(List<String> policyName, Profile profile) throws RuntimeFault, RemoteException;
}
