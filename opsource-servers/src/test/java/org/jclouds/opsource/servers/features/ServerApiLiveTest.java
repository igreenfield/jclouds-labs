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
package org.jclouds.opsource.servers.features;

import org.jclouds.opsource.servers.domain.Account;
import org.jclouds.opsource.servers.domain.DeployedServersList;
import org.jclouds.opsource.servers.domain.PendingDeployServersList;
import org.jclouds.opsource.servers.internal.BaseOpSourceServersApiLiveTest;
import org.testng.annotations.Test;

/**
 * Tests live behavior of {@link ServerApi}.
 */
@Test(groups = { "live" }, singleThreaded = true, testName = "ServerApiLiveTest")
public class ServerApiLiveTest extends BaseOpSourceServersApiLiveTest {

   public void testGetDeployedServers() {
	  Account account = restContext.getApi().getAccountApi().getMyAccount();
	  assert account.getOrgId() != null;
      DeployedServersList deployedServersList = restContext.getApi().getServerApi().getDeployedServers(account.getOrgId());
      assert deployedServersList != null;
   }
   
   public void testGetPendingDeployServers() {
	  Account account = restContext.getApi().getAccountApi().getMyAccount();
	  assert account.getOrgId() != null;
	  PendingDeployServersList pendingDeployServersList = restContext.getApi().getServerApi().getPendingDeployServers(account.getOrgId());
      assert pendingDeployServersList != null;
   }

}
