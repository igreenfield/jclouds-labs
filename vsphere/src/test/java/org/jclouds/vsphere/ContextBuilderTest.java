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


import com.google.common.collect.ImmutableSet;
import org.jclouds.ContextBuilder;
import org.jclouds.compute.ComputeServiceContext;
import org.jclouds.compute.RunNodesException;
import org.jclouds.compute.domain.NodeMetadata;
import org.jclouds.compute.domain.TemplateBuilder;
import org.jclouds.compute.options.TemplateOptions;
import org.jclouds.concurrent.config.ExecutorServiceModule;
import org.jclouds.sshj.config.SshjSshClientModule;
import org.testng.annotations.Test;

import java.util.Set;

import static com.google.common.util.concurrent.MoreExecutors.sameThreadExecutor;

/**
 * Created with IntelliJ IDEA.
 * User: igreenfi
 * Date: 2/23/14
 * Time: 12:58 PM
 * To change this template use File | Settings | File Templates.
 */

@Test(groups = "unit", testName = "ContextBuilderTest")
public class ContextBuilderTest {
    public void  testVsphereContext() throws RunNodesException {
        ImmutableSet modules = ImmutableSet.of(new ExecutorServiceModule(sameThreadExecutor(), sameThreadExecutor()), new SshjSshClientModule());
        ComputeServiceContext context = ContextBuilder.newBuilder("vsphere")
//                .credentials("root", "master1234")
//                .endpoint("https://10.56.161.100/sdk")
                .credentials("root", "vmware")
                .endpoint("https://10.45.7.70/sdk")
                .modules(modules)
                .buildView(ComputeServiceContext.class);

        TemplateBuilder b = context.getComputeService().templateBuilder();
        TemplateOptions o = context.getComputeService().templateOptions();
        o.tags(ImmutableSet.of("from UnitTest"))
                .nodeNames(ImmutableSet.of("first-vm12"))
                .runScript("cd /tmp; touch test.txt")
                .networks("VLAN537", "VLAN537")
                ;
//        b.imageId("Cisco Centos 6.5").smallest();
//        b.imageId("Cisco Centos 6.5.0").smallest().options(o);
        b.imageId("Cisco Centos 6.5").locationId("default").smallest().options(o);

       // Set images = context.getComputeService().listNodesByIds(ImmutableSet.of("junit-test-9b7"));
        Set<? extends NodeMetadata> nodes = context.getComputeService().createNodesInGroup("junit-test", 1, b.build());

        System.out.print("");
    }
}
