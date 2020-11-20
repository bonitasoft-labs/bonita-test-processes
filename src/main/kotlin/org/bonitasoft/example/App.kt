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

import com.github.javafaker.Faker
import com.bonitasoft.engine.api.APIClient
import com.bonitasoft.engine.profile.ProfileCreator
import org.bonitasoft.engine.api.ApiAccessType
import org.bonitasoft.engine.api.ProfileAPI
import org.bonitasoft.engine.bdm.BusinessObjectModelConverter
import org.bonitasoft.engine.bdm.model.BusinessObject
import org.bonitasoft.engine.bdm.model.BusinessObjectModel
import org.bonitasoft.engine.bdm.model.field.FieldType
import org.bonitasoft.engine.bdm.model.field.RelationField
import org.bonitasoft.engine.bdm.model.field.SimpleField
import org.bonitasoft.engine.identity.*
import org.bonitasoft.engine.profile.ProfileMemberCreator
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.engine.util.APITypeManager
import org.bonitasoft.example.processes.*

class App {

    fun run(url: String) {

        APITypeManager.setAPITypeAndParams(ApiAccessType.HTTP, mapOf(
                "server.url" to url,
                "application.name" to "bonita"
        ))
        val apiClient = APIClient().apply { login("install", "install") }




        /*SetupOrganization().accept(apiClient)*/

        val calledProcess = ProcessWithBigData(100).apply {
            accept(apiClient)
        }
        /*val process = ProcessWithCallActivityAborted(calledProcess.name, calledProcess.version).apply {
            accept(apiClient)
        }*/
        val callProcessXTimes = StartXProcessesWithData(calledProcess.name, calledProcess.version, 1).apply {
            accept(apiClient)
        }
//        val generatedProcessWithForms = GeneratedProcessWithForms().apply {
//            accept(apiClient)
//        }
    }
}


infix fun <T> org.bonitasoft.engine.api.APIClient.safeExec(executable: org.bonitasoft.engine.api.APIClient.() -> T): T? {
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
    App().run(args.getOrElse(0) { "http://ec2-54-75-10-130.eu-west-1.compute.amazonaws.com:8080" })
    //App().run(args.getOrElse(0) { "http://localhost:8080" })
}
