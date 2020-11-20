package org.bonitasoft.example

import com.github.javafaker.Faker
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.example.processes.*
import java.util.function.Consumer


class DeployAdminApplicationTestData : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        val faker = Faker()

        DeployEmployeeBDM().accept(apiClient)
//        SetupOrganization().accept(apiClient)
        val calledProcess = ProcessWithBigData(100).apply {
            accept(apiClient)
        }
        StartXProcessesWithData(calledProcess.name, calledProcess.version, 1).accept(apiClient)
        val formName = "custompage_${faker.dog().breed()}"
        DeployPage(formName, "form").accept(apiClient)
        (1..200).forEach {
            GeneratedProcessWithForms("${faker.animal().name()}-$it", formName).accept(apiClient)
        }
        (1..40).forEach {
            ProcessNotEnabled("${faker.rickAndMorty().character()}-$it").accept(apiClient)
        }
        (1..60).forEach {
            ProcessHavingConfigurationIssues("${faker.dune().planet()}-$it").accept(apiClient)
        }
        (1..500).forEach {
            DeployPage("custompage_${faker.dog().breed()}-$it", "form").accept(apiClient)
        }
        (1..50).forEach {
            DeployPage("custompage_${faker.dog().breed()}-$it", "apiExtension").accept(apiClient)
        }


        //TODO add process enabled but with configuration issues

    }
}