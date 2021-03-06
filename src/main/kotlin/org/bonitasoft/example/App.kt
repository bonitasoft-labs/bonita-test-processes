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
package org.bonitasoft.example

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.api.ApiAccessType
import org.bonitasoft.engine.api.ProfileAPI
import org.bonitasoft.engine.identity.Group
import org.bonitasoft.engine.identity.Role
import org.bonitasoft.engine.identity.User
import org.bonitasoft.engine.profile.ProfileMemberCreator
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.engine.util.APITypeManager
import org.bonitasoft.example.processes.ProcessWithMessageEventSubProcess

class App {

    fun run(url: String) {

        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, mapOf(
                "server.url" to url,
                "application.name" to ""
        ))
        val apiClient = APIClient().apply { login("install", "install") }

//        Organization().deploy(apiClient)
        DeployAdminApplicationTestData().accept(apiClient)
//        val businessArchive = ProcessWithMessageEventSubProcess().build()
//        BusinessArchiveFactory.writeBusinessArchiveToFile(businessArchive, File("ProcessWithMessageEventSubProcess.bar"))
    }
}


infix fun <T> org.bonitasoft.engine.api.APIClient.safeExec(executable: org.bonitasoft.engine.api.APIClient.() -> T): T? {
    return try {
        this.executable()
    } catch (e: Exception) {
        val traceElement = e.stackTrace.first { it.className.startsWith("org.bonitasoft.example") }
        println("Error: ${e.javaClass.name} ${e.message} at ${traceElement.className}.${traceElement.methodName}:${traceElement.lineNumber}")

        null
    }
}

fun ProfileAPI.getProfileByName(name: String): Long =
        searchProfiles(SearchOptionsBuilder(0, 1).filter("name", name).done()).result.first().id

fun ProfileAPI.addUserToProfile(user: User, profileName: String) {
    createProfileMember(ProfileMemberCreator(getProfileByName(profileName)).setUserId(user.id))
}

fun ProfileAPI.addGroupToProfile(group: Group, profileName: String) {
    createProfileMember(ProfileMemberCreator(getProfileByName(profileName)).setGroupId(group.id))
}

fun ProfileAPI.addRoleToProfile(role: Role, profileName: String) {
    createProfileMember(ProfileMemberCreator(getProfileByName(profileName)).setRoleId(role.id))
}

fun ProfileAPI.addMembershipToProfile(group: Group, role: Role, profileName: String) {
    createProfileMember(getProfileByName(profileName), -1L, group.id, role.id)
}

fun main(args: Array<String>) {
    App().run(args.getOrElse(0) { "http://localhost:8080" })
}
