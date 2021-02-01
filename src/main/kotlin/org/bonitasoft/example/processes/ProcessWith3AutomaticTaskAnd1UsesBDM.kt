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
package org.bonitasoft.example.processes

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression
import org.bonitasoft.example.toParameter
import org.bonitasoft.example.toScript
import java.util.*

class ProcessWith3AutomaticTaskAnd1UsesBDM : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder {
        return ProcessDefinitionBuilder().createNewInstance("ProcessWith3AutomaticTaskAnd1UsesBDM", "1.0")
                .apply {
                    addActor("theActor", true)
                    addStartEvent("start")
                    addAutomaticTask("sub1")
                    addAutomaticTask("sub2").apply {
                        addBusinessData("myEmployee", "com.company.model.Employee", """
                            def employee = new com.company.model.Employee()
                            employee.name = "name"
                            return employee
                        """.trimIndent().toScript("com.company.model.Employee"))
                    }
                    addAutomaticTask("sub3")
                    addEndEvent("end")
                    addTransition("start", "sub1")
                    addTransition("sub1", "sub2")
                    addTransition("sub2", "sub3")
                    addTransition("sub3", "end")
                }
    }


    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("walter.bates")
                })
            }
        }
    }

}