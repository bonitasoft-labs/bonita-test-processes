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
package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.form.model.FormMappingDefinition
import org.bonitasoft.engine.bpm.bar.form.model.FormMappingModel
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.form.FormMappingTarget
import org.bonitasoft.engine.form.FormMappingType

class GeneratedProcessWithForms(val processName: String, val formName: String) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance(processName, "1.0").apply {
                addActor("theActor", true)
                addStartEvent("start")
                addUserTask("Accept", "theActor")
                addUserTask("Review", "theActor")
                addUserTask("Reject", "theActor")
                addTransition("start", "Accept")
                addTransition("start", "Review")
                addTransition("start", "Reject")
            }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.setFormMappings(FormMappingModel().apply {
            addFormMapping(FormMappingDefinition(formName, FormMappingType.TASK, FormMappingTarget.INTERNAL, "Accept"))
            addFormMapping(FormMappingDefinition(formName, FormMappingType.TASK, FormMappingTarget.INTERNAL, "Review"))
            addFormMapping(FormMappingDefinition(formName, FormMappingType.TASK, FormMappingTarget.INTERNAL, "Reject"))
        })
    }
}