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

import com.google.common.base.Supplier;
import com.google.common.base.Throwables;
import com.google.common.io.Closer;
import com.vmware.vim25.CustomFieldDef;
import org.jclouds.compute.reference.ComputeServiceConstants;
import org.jclouds.logging.Logger;
import org.jclouds.vsphere.config.VSphereConstants;
import org.jclouds.vsphere.domain.VSphereServiceInstance;

import javax.annotation.Resource;
import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

@Singleton
public class CreateOrGetTagsId implements Supplier<Map<String, CustomFieldDef>> {

    @Resource
    @Named(ComputeServiceConstants.COMPUTE_LOGGER)
    protected Logger logger = Logger.NULL;

    private final Supplier<VSphereServiceInstance> serviceInstance;
    private Map<String, CustomFieldDef> customFieldDefMap = new HashMap<String, CustomFieldDef>();

    @Inject
    public CreateOrGetTagsId(Supplier<VSphereServiceInstance> serviceInstance) {

        this.serviceInstance = checkNotNull(serviceInstance, "serviceInstance");
        start();
    }

    public synchronized void start() {
        Closer closer = Closer.create();
        VSphereServiceInstance client = serviceInstance.get();
        closer.register(client);
        try {
            try {

                CustomFieldDef[] customFieldDefs = client.getInstance().getCustomFieldsManager().getField();
                if (null != customFieldDefs) {
                    for (CustomFieldDef field : customFieldDefs) {
                        if (field.getName().equalsIgnoreCase(VSphereConstants.JCLOUDS_TAGS)) {
                            customFieldDefMap.put(VSphereConstants.JCLOUDS_TAGS, field);
                        } else if (field.getName().equalsIgnoreCase(VSphereConstants.JCLOUDS_GROUP)) {
                            customFieldDefMap.put(VSphereConstants.JCLOUDS_GROUP, field);
                        }
                    }
                }
                if (!customFieldDefMap.containsKey(VSphereConstants.JCLOUDS_TAGS))
                    customFieldDefMap.put(VSphereConstants.JCLOUDS_TAGS, client.getInstance().getCustomFieldsManager().addCustomFieldDef(VSphereConstants.JCLOUDS_TAGS, null, null, null));
                if (!customFieldDefMap.containsKey(VSphereConstants.JCLOUDS_GROUP))
                    customFieldDefMap.put(VSphereConstants.JCLOUDS_GROUP, client.getInstance().getCustomFieldsManager().addCustomFieldDef(VSphereConstants.JCLOUDS_GROUP, null, null, null));
            } catch (Throwable t) {
                throw closer.rethrow(t);
            } finally {
                closer.close();
            }
        } catch (IOException e) {
            Throwables.propagate(e);
        }
    }

    @Override
    public Map<String, CustomFieldDef> get() {
        checkState(customFieldDefMap.size() > 0, "(CreateOrGetTagsId) start not called");
        return customFieldDefMap;
    }

}