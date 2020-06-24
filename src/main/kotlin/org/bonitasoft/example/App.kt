/*
 * This Kotlin source file was generated by the Gradle 'init' task.
 */
package org.bonitasoft.example

import com.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.api.ApiAccessType
import org.bonitasoft.engine.api.ProfileAPI
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.profile.ProfileMemberCreator
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.engine.util.APITypeManager
import org.bonitasoft.example.processes.CallProcessXTimes
import org.bonitasoft.example.processes.ProcessWithALotOfGateways
import org.bonitasoft.example.processes.ProcessWithOnly1Inclusive
import org.bonitasoft.example.processes.StartXProcessesEvery5Seconds

class App {

    fun run(url: String) {
        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, mapOf(
                "server.url" to url,
                "application.name" to "bonita"
        ))
        val apiClient = APIClient().apply { login("install", "install") }

        val process = ProcessWithALotOfGateways()
        val callProcessXTimes = CallProcessXTimes(process.name, process.version, 20)
        val startXProcessesEvery5Seconds = StartXProcessesEvery5Seconds(callProcessXTimes.name, callProcessXTimes.version, 200)

        listOf(
                SetupOrganization(),
                process,
                callProcessXTimes,
                startXProcessesEvery5Seconds
        ).forEach { it.accept(apiClient) }


    }
}


infix fun <T> APIClient.safeExec(executable: APIClient.() -> T): T? {
    return try {
        this.executable()
    } catch (e: Exception) {
        println("Error: ${e.javaClass.name} ${e.message}")
        null
    }
}

fun ProfileAPI.getProfileByName(name: String): Long =
        searchProfiles(SearchOptionsBuilder(0, 1).filter("name", name).done()).result.first().id

fun ProfileAPI.addUserToProfile(user: User, profileName: String) {
    createProfileMember(ProfileMemberCreator(getProfileByName(profileName)).setUserId(user.id))
}

fun main(args: Array<String>) {
    App().run(args.getOrElse(0) { "http://localhost:8080" })

}
