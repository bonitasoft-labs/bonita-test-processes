/*
 * Copyright 2020 Bonitasoft S.A.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.bonitasoft.example.datasets

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.api.ApiAccessType
import org.bonitasoft.engine.util.APITypeManager
import org.bonitasoft.example.DeployAdminApplicationTestData
import org.bonitasoft.example.NewOrganization
import org.bonitasoft.web.client.BonitaClient
import org.bonitasoft.web.client.feign.BonitaFeignClientBuilder

class PerformanceDatasetGenerator() : DatasetGenerator {
    override fun run(url: String,appName: String) {
        println("perf dataset generation ...")
        APITypeManager.setAPITypeAndParams(
            ApiAccessType.HTTP, mapOf(
            "server.url" to url,
            "application.name" to appName
        ))
        val apiClient = APIClient().apply { login("install", "install") }
        //val apiClient = BonitaClient.builder<BonitaFeignClientBuilder>(url).build()

        //login
        //val session = apiClient.login("install","install");
        // Log Bonita server version
        //println("Bonita version: "+ session.getVersion());

        DeployAdminApplicationTestData().accept(apiClient)

        //logout
        //apiClient.logout();
    }
}