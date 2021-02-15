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
import org.bonitasoft.engine.bdm.BusinessObjectModelConverter
import org.bonitasoft.engine.bdm.model.BusinessObject
import org.bonitasoft.engine.bdm.model.BusinessObjectModel
import org.bonitasoft.engine.bdm.model.field.FieldType
import org.bonitasoft.engine.bdm.model.field.RelationField
import org.bonitasoft.engine.bdm.model.field.SimpleField
import java.util.function.Consumer

class DeployEmployeeBDM : Resource {
    override fun deploy(apiClient: APIClient) {

        apiClient.tenantAdministrationAPI.pause()
        apiClient.safeExec {

            tenantAdministrationAPI.cleanAndUninstallBusinessDataModel();

            tenantAdministrationAPI.installBusinessDataModel(BusinessObjectModelConverter().zip(BusinessObjectModel().apply {
                val addresse = BusinessObject().apply {
                    qualifiedName = "com.company.model.Addresse"
                    addField(SimpleField().apply {
                        name = "street"
                        type = FieldType.STRING
                    })
                }
                addBusinessObject(addresse)
                addBusinessObject(BusinessObject().apply {
                    qualifiedName = "com.company.model.Employee"
                    addField(SimpleField().apply {
                        name = "name"
                        type = FieldType.STRING
                    })
                    addField(RelationField().apply {
                        name = "addresses"
                        type = RelationField.Type.AGGREGATION
                        reference = addresse
                    })
                })
            }));
        }

        apiClient.tenantAdministrationAPI.resume()
    }
}