package org.bonitasoft.example

import com.github.javafaker.Faker
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.example.processes.GeneratedProcessWithForms
import org.bonitasoft.example.processes.ProcessWithBigData
import org.bonitasoft.example.processes.StartXProcessesWithData
import java.util.function.Consumer


class DeployAdminApplicationTestData : Consumer<APIClient> {
    override fun accept(apiClient: APIClient) {
        val faker = Faker()
        DeployEmployeeBDM().accept(apiClient)
        SetupOrganization().accept(apiClient)
        val calledProcess = ProcessWithBigData(100).apply {
            accept(apiClient)
        }
        StartXProcessesWithData(calledProcess.name, calledProcess.version, 1).accept(apiClient)
        (1..200).forEach {
            GeneratedProcessWithForms("${faker.animal().name()}-$it").accept(apiClient)
        }

    }
}