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
package org.jclouds.vsphere.loaders;

import com.google.common.base.Optional;
import com.google.common.base.Supplier;
import com.google.common.cache.CacheLoader;
import com.google.common.io.Closer;
import com.vmware.vim25.mo.InventoryNavigator;
import com.vmware.vim25.mo.VirtualMachine;
import org.jclouds.logging.Logger;
import org.jclouds.vsphere.domain.VSphereServiceInstance;
import org.jclouds.vsphere.predicates.VSpherePredicate;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
public class VSphereTemplateLoader extends CacheLoader<String, Optional<VirtualMachine>> {
    @Resource
    protected Logger logger = Logger.NULL;

    private final Supplier<VSphereServiceInstance> serviceInstance;

    @Inject
    VSphereTemplateLoader(Supplier<VSphereServiceInstance> serviceInstance) {
        this.serviceInstance = serviceInstance;
    }

    @Override
    public Optional<VirtualMachine> load(String vmName) {
        Closer closer = Closer.create();
        VSphereServiceInstance instance = serviceInstance.get();
        closer.register(instance);
        try {
            try {
                VirtualMachine vm = (VirtualMachine) new InventoryNavigator(instance.getInstance().getRootFolder()).searchManagedEntity("VirtualMachine", vmName);
                if (VSpherePredicate.isTemplatePredicate.apply(vm)) {
                    return Optional.of(vm);
                }
            } catch (Exception e) {
                logger.error("Can't find template", e);
                throw closer.rethrow(e);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            return Optional.absent();
        }
    }
}
