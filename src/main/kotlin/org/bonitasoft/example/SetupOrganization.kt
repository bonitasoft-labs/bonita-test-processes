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
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.identity.GroupCreator
import org.bonitasoft.engine.identity.RoleCreator
import org.bonitasoft.engine.identity.UserCreator
import java.util.function.Consumer
import kotlin.math.min

class SetupOrganization : Consumer<APIClient> {

    companion object {
        val f = Faker()

        val users = mutableMapOf<String, String>()
        val groups = mutableMapOf<String, String>()
        val roles = mutableListOf<String>()
        val profiles = mutableListOf<String>()
    }

    private fun getRandomGroupPath(listOfGroups: Map<String, String>): String {
        val parentNumber = f.number().numberBetween(0, listOfGroups.size)

        if (parentNumber == 0) {
            return ""
        }

        val parentKey = listOfGroups.keys.toTypedArray()[parentNumber]
        val parentValue = listOfGroups[parentKey]

        return "$parentValue/$parentKey"
    }

    override fun accept(apiClient: APIClient) {
        // users
        for(i in 1 until 110) {
            var intermediary = f.name().username()
            while (users.contains(intermediary)) {
                intermediary = f.name().username()
            }
            users[intermediary] = f.internet().password(5, 15, true, true, true)
        }

        // groups
        for(i in 1 until 25) {
            var intermediary = f.commerce().department().replace(" &", ",")
            while (groups.contains(intermediary)) {
                intermediary = f.commerce().department().replace(" &", ",")
            }
            val path = getRandomGroupPath(groups)

            groups[intermediary] = path
        }

        // roles
        for(i in 1 until 3) {
            var intermediary = f.job().title()
            while (roles.contains(intermediary)) {
                intermediary = f.job().title()
            }
            roles.add(intermediary)
        }

        // profile
        for(i in 1 until 11) {
            var intermediary = f.commerce().department().replace(" &", ",")
            while (profiles.contains(intermediary)) {
                intermediary = f.commerce().department().replace(" &", ",")
            }
            profiles.add(intermediary)
        }

        // create all the profiles
        profiles.map {
            apiClient.safeExec {
//TODO replace by profile import
//
//                val pc = ProfileCreator(it)
//
//                val trueOrFalse = f.number().numberBetween(0, 2)
//                if (trueOrFalse == 0) {
//                    pc.setDescription(f.harryPotter().quote())
//                }
//
//                profileAPI.createProfile(pc)
            }
        }
        apiClient.safeExec {
            //TODO replace by profile import
//            profileAPI.createProfile(ProfileCreator("Administrator"))
//            profileAPI.createProfile(ProfileCreator("User"))
        }


        // create all the groups
        apiClient.safeExec {
            identityAPI.createGroup(GroupCreator("ACME"))
        }
        groups.map {
            var gc = GroupCreator(it.key)
            gc.setParentPath(it.value)
            gc.setDisplayName(it.key)
            val trueOrFalse = f.number().numberBetween(0, 2)
            if (trueOrFalse == 0) {
                gc.setDescription(f.hitchhikersGuideToTheGalaxy().quote())
            }
            apiClient.safeExec {
                identityAPI.createGroup(gc)
            }
        }.forEach {
            apiClient.safeExec {
                if (it != null) {
                    val numberOfProfilesToAdd = f.number().numberBetween(10, min(30, profiles.size))
                    val profilesWorkingList = mutableListOf<String>().apply { addAll(profiles) }

                    for (i in 0 until numberOfProfilesToAdd) {
                        val random = f.number().numberBetween(0, profilesWorkingList.size)
                        profileAPI.addGroupToProfile(it, profilesWorkingList[random])
                        profilesWorkingList.removeAt(random)
                    }
                }
            }
        }

        // create all the roles
        apiClient.safeExec {
            identityAPI.createRole("member")
        }
        roles.map {
            val rc = RoleCreator(it)
            rc.setDisplayName(it)
            val trueOrFalse = f.number().numberBetween(0, 2)
            if (trueOrFalse == 0) {
                rc.setDescription(f.howIMetYourMother().quote())
            }
            apiClient.safeExec {
                identityAPI.createRole(rc)
            }
        }.forEach {
            apiClient.safeExec {
                if (it != null) {
                    val numberOfProfilesToAdd = f.number().numberBetween(50, min(100, profiles.size))
                    val profilesWorkingList = mutableListOf<String>().apply { addAll(profiles) }

                    for (i in 0 until numberOfProfilesToAdd) {
                        val random = f.number().numberBetween(0, profilesWorkingList.size)
                        profileAPI.addRoleToProfile(it, profilesWorkingList[random])
                        profilesWorkingList.removeAt(random)
                    }
                }
            }
        }

        // add memberships to profiles
        profiles.map {
            apiClient.safeExec {
                val numberOfMemberships = f.number().numberBetween(1, min(20, groups.size))

                val groupsWorkingList = mutableMapOf<String, String>().apply { putAll(groups) }
                for(i in 0 until numberOfMemberships) {
                    var randomGroup = f.number().numberBetween(0, groupsWorkingList.size)
                    var randomGroupPath = groupsWorkingList.values.toTypedArray()[randomGroup]
                    while (randomGroupPath == "") {
                        groupsWorkingList.remove(groupsWorkingList.keys.toTypedArray()[randomGroup])
                        randomGroup = f.number().numberBetween(0, groupsWorkingList.size)
                        randomGroupPath = groupsWorkingList.values.toTypedArray()[randomGroup]
                    }
                    val randomRole = f.number().numberBetween(0, roles.size)
                    val randomRoleName = roles[randomRole]

                    profileAPI.addMembershipToProfile(identityAPI.getGroupByPath(randomGroupPath), identityAPI.getRoleByName(randomRoleName), it)

                    groupsWorkingList.remove(groupsWorkingList.keys.toTypedArray()[randomGroup])
                }
            }
        }

        // create all users
        val user = apiClient.safeExec {
            identityAPI.createUser(UserCreator("walter.bates", "bpm").apply {
                setFirstName("Walter")
                setLastName("Bates")
            })
        }
        users.map {
            val managerUserIdPosition = f.number().numberBetween(0, users.keys.indexOf(it.key))
            val managerId: Long
            if (users.keys.indexOf(it.key) == 0) {
                managerId = apiClient.identityAPI.getUserByUserName("walter.bates").id
            } else {
                managerId = apiClient.identityAPI.getUserByUserName(users.keys.toTypedArray()[managerUserIdPosition]).id
            }
            apiClient.safeExec {
                val isEnabled = f.number().numberBetween(0, 1)
                identityAPI.createUser(UserCreator(it.key, it.value).apply {
                    if (isEnabled == 0) {
                        setEnabled(false)
                    }
                    setFirstName(it.key.split(".").first().capitalize())
                    setLastName(it.key.split(".").last().capitalize())
                    setJobTitle(f.job().title())
                    setManagerUserId(managerId)
                })
            }
        }.forEach {
            apiClient.safeExec {
                if (it != null) {
                    val numberOfProfilesToAdd = f.number().numberBetween(1, min(20, profiles.size))
                    val profilesWorkingList = mutableListOf<String>().apply { addAll(profiles) }

                    for(i in 0 until numberOfProfilesToAdd) {
                        val random = f.number().numberBetween(0, profilesWorkingList.size)
                        profileAPI.addUserToProfile(it, profilesWorkingList[random])
                        profilesWorkingList.removeAt(random)
                    }

                    val numberOfMemberships = f.number().numberBetween(1, min(20, groups.size))

                    val groupsWorkingList = mutableMapOf<String, String>().apply { putAll(groups) }
                    for(i in 0 until numberOfMemberships) {
                        val randomGroup = f.number().numberBetween(0, groupsWorkingList.size)
                        val randomRole = f.number().numberBetween(0, roles.size)

                        identityAPI.addUserMembership(it.id, identityAPI.getGroupByPath(groupsWorkingList[groupsWorkingList.keys.toTypedArray()[randomGroup]] + "/" + groupsWorkingList.keys.toTypedArray()[randomGroup]).id,
                                identityAPI.getRoleByName(roles[randomRole]).id)
                        groupsWorkingList.remove(groupsWorkingList.keys.toTypedArray()[randomGroup])
                    }
                }
            }
        }

        apiClient.safeExec {
            if (user != null) {
                profileAPI.addUserToProfile(user, "Administrator")
                profileAPI.addUserToProfile(user, "User")
                identityAPI.addUserMembership(user.id, identityAPI.getGroupByPath("/ACME").id, identityAPI.getRoleByName("member").id)
            }
        }
    }
}
