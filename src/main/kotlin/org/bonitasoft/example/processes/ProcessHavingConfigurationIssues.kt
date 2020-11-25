package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.connector.ConnectorEvent
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder

class ProcessHavingConfigurationIssues(val processName: String) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance(processName, "1.0").apply {
                addActor("theActor", true)
                addStartEvent("start")
                addUserTask("Accept", "theActor")
                addUserTask("Review", "theActor")
                addUserTask("Reject", "theActor").addConnector("someConnector", "unexistingConnectorId", "1.0", ConnectorEvent.ON_ENTER)
                addTransition("start", "Accept")
                addTransition("start", "Review")
                addTransition("start", "Reject")
            }

}