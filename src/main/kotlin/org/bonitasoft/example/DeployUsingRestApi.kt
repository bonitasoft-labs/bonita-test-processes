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
package org.bonitasoft.example

import org.bonitasoft.engine.bpm.bar.BusinessArchiveFactory
import org.bonitasoft.example.processes.GeneratedProcessWithForms
import org.bonitasoft.web.client.BonitaClient
import org.bonitasoft.web.client.services.policies.ProcessImportPolicy
import java.nio.file.Files

class DeployUsingRestApi {

    fun run(url: String) {
        val client = BonitaClient.builder(url)
                .build()


        client.login("install", "install")
        val barFile = Files.createTempFile("GeneratedProcessWithForms",".bar").toFile()
        barFile.delete()
        BusinessArchiveFactory.writeBusinessArchiveToFile(GeneratedProcessWithForms().businessArchive, barFile)


        client.processes().importProcess(barFile, ProcessImportPolicy.REPLACE_DUPLICATES)

    }
}




fun main(args: Array<String>) {
    DeployUsingRestApi().run(args.getOrElse(0) { "http://ec2-54-75-10-130.eu-west-1.compute.amazonaws.com:8080/bonita" })
    //App().run(args.getOrElse(0) { "http://localhost:8080" })
}
