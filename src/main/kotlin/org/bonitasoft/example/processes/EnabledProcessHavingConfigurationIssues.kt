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

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.actor.ActorCriterion
import org.bonitasoft.engine.bpm.connector.ConnectorEvent
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.example.safeExec

class EnabledProcessHavingConfigurationIssues(val processName: String) : BonitaProcess() {
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

    override fun deploy(client: APIClient) {
        super.deploy(client)
        client.safeExec {
            val actor = processAPI.getActors(processDefinitionId!!, 0, 1, ActorCriterion.NAME_ASC)
            processAPI.removeActorMember(processAPI.getActorMembers(actor[0].id, 0, 1)[0].id)
        }
    }
}
