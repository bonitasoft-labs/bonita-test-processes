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
import org.bonitasoft.example.datasets.Dataset
import org.bonitasoft.example.datasets.DevDatasetGenerator
import org.bonitasoft.example.datasets.PerformanceDatasetGenerator
import org.bonitasoft.example.processes.ProcessWithMessageEventSubProcess
import picocli.CommandLine
import java.net.URL
import java.util.concurrent.Callable
import kotlin.system.exitProcess

@CommandLine.Command(name = "dataset", mixinStandardHelpOptions = true, version = ["dataset 0.0.1"],
    description = ["Generate bonita datasets"])
class BonitaDatasetCLI : Callable<Int> {

    @CommandLine.Parameters(paramLabel = "Dataset", description = ["The dataset ID you want to generate"])
    lateinit var datasetID: Dataset

    @CommandLine.Option(names = ["-h", "--url"], paramLabel = "Bonita URL", description = ["Destination Bonita URL where dataset will be generated"], interactive = true)
    var bonitaURL: String = "http://localhost:8080"

    @CommandLine.Option(names = ["-a", "--app"], paramLabel = "Bonita App Name", description = ["Bonita App name where dataset will be generated"], interactive = true)
    var appName: String = "bonita"


    override fun call(): Int {
        when(datasetID){
            Dataset.DEV -> DevDatasetGenerator().run(bonitaURL, appName);
            Dataset.PERF -> PerformanceDatasetGenerator().run(bonitaURL, appName);
        }
        return 0;
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

fun main(args: Array<String>) : Unit = exitProcess(CommandLine(BonitaDatasetCLI()).execute(*args))
