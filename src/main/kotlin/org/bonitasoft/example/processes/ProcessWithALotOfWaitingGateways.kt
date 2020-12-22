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

import org.bonitasoft.engine.bpm.flownode.GatewayType
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class ProcessWithALotOfWaitingGateways() : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance(this.javaClass.simpleName, "1,0")
                    .apply {
                        addStartEvent("start")
                        addAutomaticTask("auto1")
                        addAutomaticTask("auto2")
                        addAutomaticTask("auto3")
                        addAutomaticTask("auto4")
                        addAutomaticTask("auto5")
                        addAutomaticTask("auto6")
                        addAutomaticTask("auto7")
                        addAutomaticTask("auto8")
                        addAutomaticTask("auto9")
                        addAutomaticTask("auto10")
                        addGateway("para1", GatewayType.PARALLEL)
                        addGateway("para2", GatewayType.PARALLEL)
                        addGateway("para3", GatewayType.PARALLEL)
                        addGateway("para4", GatewayType.PARALLEL)
                        addGateway("para5", GatewayType.PARALLEL)
                        addGateway("para6", GatewayType.PARALLEL)
                        addGateway("para7", GatewayType.PARALLEL)
                        addGateway("para8", GatewayType.PARALLEL)

                        addTransition("start", "auto1")
                        addTransition("start", "para1")
                        addTransition("start", "para2")
                        addTransition("start", "para3")
                        addTransition("start", "para4")
                        addTransition("start", "para5")
                        addTransition("start", "para6")
                        addTransition("start", "para7")
                        addTransition("start", "para8")
                        addTransition("auto10", "para1")
                        addTransition("auto10", "para2")
                        addTransition("auto10", "para3")
                        addTransition("auto10", "para4")
                        addTransition("auto10", "para5")
                        addTransition("auto10", "para6")
                        addTransition("auto10", "para7")
                        addTransition("auto10", "para8")

                        addTransition("auto1", "auto2")
                        addTransition("auto2", "auto3")
                        addTransition("auto3", "auto4")
                        addTransition("auto4", "auto5")
                        addTransition("auto5", "auto6")
                        addTransition("auto6", "auto7")
                        addTransition("auto7", "auto8")
                        addTransition("auto8", "auto9")
                        addTransition("auto9", "auto10")


                    }}