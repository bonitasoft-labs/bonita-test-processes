/*
 * Copyright 2021 Bonitasoft S.A.
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
package org.bonitasoft.example

import com.github.javafaker.Faker
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.example.processes.*
import java.util.function.Consumer


class DeployAdminApplicationTestData : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        val faker = Faker()

        DeployEmployeeBDM().deploy(apiClient)
        Organization().deploy(apiClient)
        val calledProcess = BigData(100).apply {
            deploy(apiClient)
        }
        StartXProcessesWithData(calledProcess.name, calledProcess.version, 1).deploy(apiClient)
        val formName = "custompage_${faker.dog().breed()}"
        GeneratedPage(formName, "form").deploy(apiClient)
        (1..200).forEach {
            GeneratedProcessWithForms("GeneratedProcessWithForms-${faker.animal().name()}-$it", formName).deploy(apiClient)
        }
        (1..40).forEach {
            ProcessNotEnabled("ProcessNotEnabled-${faker.rickAndMorty().character()}-$it").deploy(apiClient)
        }
        (1..60).forEach {
            ProcessHavingConfigurationIssues("ProcessHavingConfigurationIssues-${faker.dune().planet()}-$it").deploy(apiClient)
        }
        (1..500).forEach {
            GeneratedPage("custompage_${faker.dog().breed().replace(" ", "")}$it", "form").deploy(apiClient)
        }
        (1..50).forEach {
            GeneratedRestApiExt("custompage_${faker.dog().name().replace(" ", "")}$it").deploy(apiClient)
        }
        (1..10).forEach {
            GeneratedApplication("custompage_${faker.dog().name().replace(" ", "")}$it").deploy(apiClient)
        }
        (1..150).forEach {
            EnabledProcessHavingConfigurationIssues("EnabledProcessHavingConfigurationIssues-${faker.animal().name()}-$it").deploy(apiClient)
        }
    }
}
