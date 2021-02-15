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

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.page.PageCreator
import java.nio.file.Files
import java.nio.file.Paths
import java.util.function.Consumer

class GeneratedPage(val pageName: String, val type: String) : Resource {
    override fun deploy(apiClient: APIClient) {
        apiClient.safeExec {
            customPageAPI.createPage(PageCreator(pageName, "custom_form.zip").apply {
                setContentType(type)
                setDescription("This is a form generated using the bonita-test-processes, an awesome project")
                setDisplayName("GeneratedForm $pageName")
            }, Files.readAllBytes(Paths.get(GeneratedPage::class.java.getResource("/case-autogenerated-overview.zip").toURI())))
        }
    }
}