package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.business.application.ApplicationImportPolicy
import java.nio.file.Files
import java.nio.file.Paths

class GeneratedApplication(val applicationName: String) : Resource {
    override fun deploy(apiClient: APIClient) {


        val content = String(Files.readAllBytes(Paths.get(GeneratedPage::class.java.getResource("/applicationDescriptorFile.xml").toURI())))

        apiClient.applicationAPI.importApplications(content.replace("\$APP_NAME", applicationName).toByteArray(), ApplicationImportPolicy.REPLACE_DUPLICATES)


    }

}