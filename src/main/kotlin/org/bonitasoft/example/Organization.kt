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

import com.bonitasoft.engine.profile.ProfileCreator
import com.github.javafaker.Faker
import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.identity.GroupCreator
import org.bonitasoft.engine.identity.RoleCreator
import org.bonitasoft.engine.identity.UserCreator
import kotlin.math.min

class Organization : Resource {

    companion object {
        val f = Faker()
    }

    private fun getRandomGroupPath(listOfGroups: LinkedHashMap<String, String>) : String {
        var parentNumber = f.number().numberBetween(0, listOfGroups.size)

        if (parentNumber == 0) {
            return ""
        }

        var parentKey = listOfGroups.keys.toTypedArray()[parentNumber]
        var parentValue = listOfGroups[parentKey]

        return "$parentValue/$parentKey"
    }

    override fun deploy(apiClient: APIClient) {


        val users = LinkedHashMap<String, String>() // f.name().username()
        val groups = LinkedHashMap<String, String>() // f.commerce().department()
        val roles = ArrayList<String>() // f.job().title()
        val profiles = ArrayList<String>() // f.commerce().department()
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
            profiles.add(f.commerce().department().replace(" &", ",") + "-" + i)
        }

        // create all the profiles
        profiles.map {
            apiClient.safeExec {
                val apiClientSp = this as com.bonitasoft.engine.api.APIClient

                val profileAPI = apiClientSp.profileAPI

                var pc = ProfileCreator(it)

                val trueOrFalse = f.number().numberBetween(0, 2)
                if (trueOrFalse == 0) {
                    pc.setDescription(f.harryPotter().quote())
                }

                profileAPI.createProfile(pc)
            }
        }
        apiClient.safeExec {
            val apiClientSp = this as com.bonitasoft.engine.api.APIClient
            apiClientSp.profileAPI.createProfile(ProfileCreator("Administrator"))
            apiClientSp.profileAPI.createProfile(ProfileCreator("User"))
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
                    val numberOfProfilesToAdd = f.number().numberBetween(10, 30)
                    for (i in 0 until numberOfProfilesToAdd) {
                        val random = f.number().numberBetween(0, profiles.size)
                        profileAPI.addGroupToProfile(it, profiles[random])
                    }
                }
            }
        }

        // create all the roles
        apiClient.safeExec {
            identityAPI.createRole("member")
        }
        roles.map {
            var rc = RoleCreator(it)
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
                    for (i in 0 until numberOfProfilesToAdd) {
                        val random = f.number().numberBetween(0, profiles.size)
                        profileAPI.addRoleToProfile(it, profiles[random])
                    }
                }
            }
        }

        // add memberships to profiles
        profiles.map {
            apiClient.safeExec {
                val numberOfMemberships = f.number().numberBetween(1, min(20, groups.size))

                var groupsWorkingList = groups.clone() as LinkedHashMap<String, String>
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
            var managerUserIdPosition = f.number().numberBetween(0, users.keys.indexOf(it.key))
            var managerId: Long
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
                    val numberOfProfilesToAdd = f.number().numberBetween(1,  min(20, profiles.size))
                    var profilesWorkingList = profiles.clone() as ArrayList<String>

                    for(i in 0 until numberOfProfilesToAdd) {
                        val random = f.number().numberBetween(0, profilesWorkingList.size)
                        profileAPI.addUserToProfile(it, profilesWorkingList[random])
                        profilesWorkingList.removeAt(random)
                    }

                    val numberOfMemberships = f.number().numberBetween(1, min(20, groups.size))

                    var groupsWorkingList = groups.clone() as LinkedHashMap<String, String>
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
