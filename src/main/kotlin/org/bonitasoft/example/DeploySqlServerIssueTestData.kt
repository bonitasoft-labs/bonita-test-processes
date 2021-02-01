package org.bonitasoft.example

import org.awaitility.kotlin.*
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.identity.GroupCreator
import org.bonitasoft.engine.identity.UserCreator
import org.bonitasoft.example.processes.CallProcessXTimes
import org.bonitasoft.example.processes.ProcessWith3AutomaticTaskAnd1UsesBDM
import org.nield.kotlinstatistics.descriptiveStatistics
import org.nield.kotlinstatistics.median
import java.time.Duration
import java.time.Instant
import java.util.function.Consumer

class DeploySqlServerIssueTestData : Consumer<APIClient> {

    fun buildLightOrga(apiClient: APIClient) {
        val user = apiClient.safeExec {
            identityAPI.createUser(UserCreator("walter.bates", "bpm").apply {
                setFirstName("Walter")
                setLastName("Bates")
            })
        }

        // create all the groups
        apiClient.safeExec {
            identityAPI.createGroup(GroupCreator("ACME"))
        }

        // create all the roles
        apiClient.safeExec {
            identityAPI.createRole("member")
        }

        apiClient.safeExec {
            if (user != null) {
                profileAPI.addUserToProfile(user, "Administrator")
                profileAPI.addUserToProfile(user, "User")
                identityAPI.addUserMembership(
                    user.id,
                    identityAPI.getGroupByPath("/ACME").id,
                    identityAPI.getRoleByName("member").id
                )
            }
        }
    }

    override fun accept(apiClient: APIClient) {


        DeployEmployeeBDM().deploy(apiClient)
        buildLightOrga(apiClient)

        val caller = ProcessWith3AutomaticTaskAnd1UsesBDM().apply {
            deploy(apiClient)
        }
        val callProcess = CallProcessXTimes(caller.name, caller.version, 1000).apply {
            deploy(apiClient)
        }
        val processDurationTimes = ArrayList<Duration>();
        repeat(20) {
            println("waiting for the execution end of process  ${callProcess.name}")

            val duration = apiClient.safeExec {
                println("Start run $it at ${Instant.now()}")
                val processInstance = processAPI.startProcess(callProcess.processDefinitionId!!)
                await atMost Duration.ofMinutes(10) untilNotNull {
                    processAPI.getFinalArchivedProcessInstance(
                        processInstance.id
                    ).endDate
                }

                val archivedProcessInstance = processAPI.getFinalArchivedProcessInstance(processInstance.id);

                Duration.between(
                    archivedProcessInstance.startDate.toInstant(),
                    archivedProcessInstance.endDate.toInstant()
                )
            }
            processDurationTimes.add(duration!!);
            println("execution timeStamp $duration ")
        }

        val executionTimes = processDurationTimes.map { it.toMillis() }
        val median = executionTimes.median()

        println("execution time average $median ms}");

    }
}