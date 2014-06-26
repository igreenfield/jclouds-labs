package org.jclouds.vsphere.predicates;

import com.vmware.vim25.mo.VirtualMachine;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Date: 26/06/2014 9:18 AM
 * Package: org.jclouds.vsphere.predicates
 */
@Test(groups = "unit", testName = "VSpherePredicateTest")
public class VSpherePredicateTest {
    public void isTemplatePredicateTest() {
        Assert.assertTrue(VSpherePredicate.isTemplatePredicate.apply(new VirtualMachine(null, null)));
    }

    public void isInet4AddressTest() {
        Assert.assertTrue(VSpherePredicate.isInet4Address.apply("10.45.37.3"));
        Assert.assertTrue(!VSpherePredicate.isInet4Address.apply("fd7f:628d:cd1e:1:499e:4dbc:ca33:13cf"));
    }

    public void isInet6AddressTest() {
        Assert.assertTrue(!VSpherePredicate.isInet6Address.apply("10.45.37.3"));
        Assert.assertTrue(VSpherePredicate.isInet6Address.apply("fd7f:628d:cd1e:1:499e:4dbc:ca33:13cf"));
    }

    public void wait_for_nicTest() {
        long start = System.currentTimeMillis();
        Assert.assertTrue(!VSpherePredicate.WAIT_FOR_NIC.apply(new VirtualMachine(null, null)));
        Assert.assertTrue(System.currentTimeMillis() - start > 5 * 1000 * 10);
    }

    public void wait_for_vmtoolsTest() {
        long start = System.currentTimeMillis();
        Assert.assertTrue(!VSpherePredicate.WAIT_FOR_VMTOOLS.apply(new VirtualMachine(null, null)));
        Assert.assertTrue(System.currentTimeMillis() - start > 10 * 1000 * 10);
    }


}
