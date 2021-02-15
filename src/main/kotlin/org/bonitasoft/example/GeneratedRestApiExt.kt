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
import java.io.ByteArrayOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

class GeneratedRestApiExt(val pageName: String) : Resource {
    override fun deploy(apiClient: APIClient) {

        apiClient.customPageAPI.createPage(PageCreator(pageName, "custom_rest_api_$pageName.zip").apply {
            setContentType("apiExtension")
            setDisplayName("Generated page $pageName")
        }, zip(mutableMapOf(
                "page.properties" to """
                    displayName=$pageName
                    contentType=apiExtension
                    apiExtensions=$pageName
                    name=custompage_$pageName
                    $pageName.method=GET
                    $pageName.classFileName=Index.groovy
                    $pageName.pathTemplate=demo/$pageName
                    $pageName.permissions=demoPermission
                    """.trimIndent().toByteArray(),
                "Index.groovy" to """
                    import groovy.json.JsonBuilder
                    import javax.servlet.http.HttpServletRequest
                    import org.bonitasoft.web.extension.rest.*

                    class Index implements RestApiController {

                        @Override
                        RestApiResponse doHandle(HttpServletRequest request, RestApiResponseBuilder apiResponseBuilder, RestAPIContext context) {
                            Map<String, String> response = [:]
                            response.put "response", "hello"
                            response.putAll request.parameterMap
                            apiResponseBuilder.with {
                                withResponse new JsonBuilder(response).toString()
                                build()
                            }
                        }

                    }
                    """.trimIndent().toByteArray()
        )))

    }

    fun zip(files: Map<String, ByteArray>): ByteArray {
        val baos = ByteArrayOutputStream()
        ZipOutputStream(baos).use { zos ->
            for ((key, value) in files) {
                zos.putNextEntry(ZipEntry(key))
                zos.write(value)
                zos.closeEntry()
            }
            return baos.toByteArray()
        }
    }

}