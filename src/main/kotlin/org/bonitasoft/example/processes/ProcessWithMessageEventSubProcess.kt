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

import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.toExpression

class ProcessWithMessageEventSubProcess : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder {
        return ProcessDefinitionBuilder().createNewInstance("ProcessWithMessageEventSubProcess", "1.0")
                .apply {
                    addActor("theActor", true)
                    addUserTask("t1", "theActor").addDisplayName("Task 1".toExpression())
                    addSubProcess("SubProcessThatStartsWithMessage", true).subProcessBuilder.apply {
                        addStartEvent("startMessage").apply {
                            addMessageEventTrigger("theMessage").apply {
                                addCorrelation("key1".toExpression(), "TheValue1".toExpression())
                                addCorrelation("key2".toExpression(), "TheValue2".toExpression())
                                addCorrelation("key3".toExpression(), "TheValue3".toExpression())
                                addCorrelation("key4".toExpression(), "TheValue4".toExpression())
                                addCorrelation("key5".toExpression(), "TheValue5".toExpression())
                            }

                        }
                        addUserTask("taskInMessageSubProcess", "theActor")
                        addTransition("startMessage", "taskInMessageSubProcess")
                    }
                    addSubProcess("SubProcessThatStartsWithError", true).subProcessBuilder.apply {
                        addStartEvent("startError").apply {
                            addErrorEventTrigger("theError")
                        }
                        addUserTask("taskInErrorSubProcess", "theActor")
                        addTransition("startError", "taskInErrorSubProcess")
                    }
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
