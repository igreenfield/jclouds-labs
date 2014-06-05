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

import com.google.common.collect.ImmutableSet;
import com.google.inject.Injector;
import expectj.ExpectJ;
import expectj.Spawn;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.jclouds.vsphere.compute.options.VSphereTemplateOptions;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

/**
 * CISCO LTD.
 * User: igreenfi
 * Date: 20/05/2014 3:12 PM
 * Package: org.jclouds.vsphere
 */

@Test(testName = "ConductorDeployment", threadPoolSize = 3, singleThreaded = false,suiteName = "conductor")
public class ConductorDeploymentTest {

    @BeforeClass
    public void beforeClass() throws IOException {
        ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());
        Injector injector = ContextBuilder.newBuilder("vsphere")
                .credentials("root", "vmware")
                .endpoint("https://10.45.7.70/sdk")
                .modules(modules)
                .buildInjector();

        IFileManager fileManagerApi = injector.getInstance(IFileManager.class);
        fileManagerApi.uploadFile("D:/SVN/conductor-qa/testbed/izek-voo/infra.flp", "ISO/infra.flp");
        fileManagerApi.uploadFile("D:/SVN/conductor-qa/testbed/izek-voo/cmc.flp", "ISO/cmc.flp");
        fileManagerApi.uploadFile("D:/SVN/conductor-qa/testbed/izek-voo/service.flp", "ISO/service.flp");
    }

    public void  testCreateCmcNode() throws RunNodesException {
        ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());
        ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
                .credentials("root", "vmware")
                .endpoint("https://10.45.7.70/sdk")
                .modules(modules)
                .buildView(ComputeServiceContext.class);

        TemplateBuilder b = context.getComputeService().templateBuilder();
        TemplateOptions o = context.getComputeService().templateOptions();
        ((VSphereTemplateOptions)o).isoFileName("ISO/UCSInstall_UCOS_3.1.0.0-9914.iso");
        ((VSphereTemplateOptions)o).flpFileName("ISO/cmc.flp");
        ((VSphereTemplateOptions)o).postConfiguration(false);
        o.tags(ImmutableSet.of("from UnitTest"))
                .nodeNames(ImmutableSet.of("con-cmc-node"))
                .networks("VLAN537");
        b.imageId("conductor-mgt").locationId("default").minRam(6000).options(o);

        Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("junit-test", 1, b.build());

        System.out.print("");
    }
    public void  testCreateInfraNode() throws RunNodesException {
        ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());
        ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
                .credentials("root", "vmware")
                .endpoint("https://10.45.7.70/sdk")
                .modules(modules)
                .buildView(ComputeServiceContext.class);

        TemplateBuilder b = context.getComputeService().templateBuilder();
        TemplateOptions o = context.getComputeService().templateOptions();
        ((VSphereTemplateOptions)o).isoFileName("ISO/UCSInstall_UCOS_3.1.0.0-9914.iso");
        ((VSphereTemplateOptions)o).flpFileName("ISO/infra.flp");
        ((VSphereTemplateOptions)o).postConfiguration(false);
        o.tags(ImmutableSet.of("from UnitTest"))
                .nodeNames(ImmutableSet.of("con-infra-node"))
                .networks("VLAN537");
        b.imageId("conductor-mgt").locationId("default").minRam(6000).options(o);

        Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("junit-test", 1, b.build());

        System.out.print("");
    }
    public void  testCreateServiceNode() throws RunNodesException {
        ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());
        ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
                .credentials("root", "vmware")
                .endpoint("https://10.45.7.70/sdk")
                .modules(modules)
                .buildView(ComputeServiceContext.class);

        TemplateBuilder b = context.getComputeService().templateBuilder();
        TemplateOptions o = context.getComputeService().templateOptions();
        ((VSphereTemplateOptions)o).isoFileName("ISO/UCSInstall_UCOS_3.1.0.0-9914.iso");
        ((VSphereTemplateOptions)o).flpFileName("ISO/service.flp");
        ((VSphereTemplateOptions)o).postConfiguration(false);
        o.tags(ImmutableSet.of("from UnitTest"))
                .nodeNames(ImmutableSet.of("con-service-node"))
                .networks("VLAN537");
        b.imageId("conductor-mgt").locationId("default").minRam(6000).options(o);

        Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("junit-test", 1, b.build());

        System.out.print("");
    }

    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    public void testCipher() throws Exception {
        byte[] b_key = { (byte)239, 0, (byte)255, 2, (byte)251, 0, (byte)255, 66 };
        String bin_key = "1110111100000000111111110000001011111011000000001111111101000010";
        String hex_key = "EF00FF02FB00FF42";
        //String hex_key = "24FF00BF02FF00Fe";
        BigInteger key = new BigInteger(hex_key, 16);
        DESKeySpec dks = new DESKeySpec(b_key);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);

        Cipher desCipher;

        // Create the cipher
        desCipher = Cipher.getInstance("DES");

        // Initialize the cipher for encryption
        desCipher.init(Cipher.ENCRYPT_MODE, desKey);

        //sensitive information

        byte[] text = "roZes123\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0\0".getBytes();

        System.out.println("Text [Byte Format] : " + text);
        System.out.println("Text : " + new String(text));

        // Encrypt the text
        byte[] textEncrypted = desCipher.doFinal(text);

        //4A308FD736063EF2831812812AB2825C831812812AB2825C831812812AB2825C

        //4A308FD736063EF2831812812AB2825C831812812AB2825C831812812AB2825C
        //4A308FD736063EF2831812812AB2825C831812812AB2825C831812812AB2825C

        //B5503BAE35F5BD85C1110B43CDB0B8DAC1110B43CDB0B8DAC1110B43CDB0B8DA
        System.out.println("Text Encryted : " + bytesToHex(textEncrypted).substring(0,64));

        // Initialize the same cipher for decryption
        desCipher.init(Cipher.DECRYPT_MODE, desKey);

        // Decrypt the text
        byte[] textDecrypted = desCipher.doFinal(textEncrypted);

        System.out.println("Text Decryted : " + new String(textDecrypted));
    }


    public void testInstallCMC() throws Exception {
        ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());
        ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
                .credentials("root", "vmware")
                .endpoint("https://10.45.7.70/sdk")
                .modules(modules)
                .buildView(ComputeServiceContext.class);
        NodeMetadata node = context.getComputeService().getNodeMetadata("con-cmc-node");
        //, "", RunScriptOptions.Builder.overrideLoginUser("admin") .overrideLoginPassword("roZes123") .blockOnPort(7300, 86400));
        node.getCredentials();

        ExpectJ expectJ = new ExpectJ(1000000000);
        Spawn sshj = expectJ.spawn("10.45.38.20", 22, "admin", "roZes123");
        sshj.expect("admin:");
        sshj.send("file transfer secure-import copuser@10.56.176.237:/home/copuser/cisco.conductor-cmc-3.1-0-1877.cop.sgn\n");
        sshj.expect("admin:");
        sshj.send("file load cop cisco.conductor-cmc-3.1-0-1877.cop.sgn\n");
        sshj.expect("admin:");

    }

}
