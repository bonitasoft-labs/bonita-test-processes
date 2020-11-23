package org.bonitasoft.example

import com.github.javafaker.Faker
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.example.processes.*
import java.util.function.Consumer


class DeployAdminApplicationTestData : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        val faker = Faker()
//
//        DeployEmployeeBDM().deploy(apiClient)
////        SetupOrganization().accept(apiClient)
//        val calledProcess = BigData(100).apply {
//            deploy(apiClient)
//        }
//        StartXProcessesWithData(calledProcess.name, calledProcess.version, 1).deploy(apiClient)
//        val formName = "custompage_${faker.dog().breed()}"
//        GeneratedPage(formName, "form").deploy(apiClient)
//        (1..200).forEach {
//            GeneratedProcessWithForms("${faker.animal().name()}-$it", formName).deploy(apiClient)
//        }
//        (1..40).forEach {
//            ProcessNotEnabled("${faker.rickAndMorty().character()}-$it").deploy(apiClient)
//        }
//        (1..60).forEach {
//            ProcessHavingConfigurationIssues("${faker.dune().planet()}-$it").deploy(apiClient)
//        }
//        (1..500).forEach {
//            GeneratedPage("custompage_${faker.dog().breed()}$it", "form").deploy(apiClient)
//        }
//        (1..50).forEach {
//            GeneratedRestApiExt("custompage_${faker.dog().name().replace(" ", "")}$it").deploy(apiClient)
//        }
        (1..10).forEach {
            GeneratedApplication("custompage_${faker.dog().name().replace(" ", "")}$it").deploy(apiClient)
        }


        //TODO add process enabled but with configuration issues

    }
}