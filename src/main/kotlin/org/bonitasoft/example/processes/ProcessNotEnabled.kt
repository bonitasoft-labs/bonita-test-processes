package org.bonitasoft.example.processes

import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder

class ProcessNotEnabled(val processName: String) : BonitaProcess(false) {
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

}