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
package org.bonitasoft.example.processes

import org.bonitasoft.engine.api.APIClient
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.contract.Type
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.expression.ExpressionConstants
import org.bonitasoft.engine.form.FormMappingSearchDescriptor
import org.bonitasoft.engine.form.FormMappingType
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.engine.page.PageNotFoundException
import org.bonitasoft.engine.search.SearchOptionsBuilder
import org.bonitasoft.example.safeExec
import org.bonitasoft.example.toExpression
import org.bonitasoft.example.toParameter
import org.bonitasoft.example.toScript
import java.lang.Exception

class StartXProcessesWithData(private val targetProcessName: String, private val targetProcessVersion: String, private val instances: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("StartXProcesses", "1.1")
                    .apply {
                        addContract().apply {
                            addInput("numberOfInstances", Type.INTEGER, "number of processes to start")
                        }
                        addIntegerData("instances", ExpressionBuilder().createContractInputExpression("numberOfInstances", "java.lang.Integer"))
                        addParameter("targetProcessName", "java.lang.String")
                        addParameter("targetProcessVersion", "java.lang.String")
                        addActor("theActor", true)
                        addStartEvent("start")
                        addShortTextData("someText", null)
                        addAutomaticTask("task1").apply {
                            addMultiInstance(false, ExpressionBuilder().createDataExpression("instances", "java.lang.Integer"))
                            addOperation(OperationBuilder().createSetDataOperation("someText", """
                                  import org.bonitasoft.engine.bpm.contract.FileInputValue;

                                def user =  apiAccessor.identityAPI.getUserByUserName("walter.bates")
                                
                                def pId = apiAccessor.processAPI.getProcessDefinitionId(targetProcessName, targetProcessVersion)
                                ArrayList<Serializable> createList = new ArrayList<>(java.util.Arrays.asList(
                                        new FileInputValue("file1", "text/plain", "the content".getBytes()),
                                        new FileInputValue("file2", "text/plain", "the content".getBytes()),
                                        new FileInputValue("file3", "text/plain", "the content".getBytes())));
                                
                                
                                def contractInputs=[fileInputValues: createList,
                                                    file1: new FileInputValue("file1", "text/plain", "the content".getBytes()),
                                                    file2: new FileInputValue("file2", "text/plain", "the content".getBytes()) ]
                                def processInstance = apiAccessor.processAPI.startProcessWithInputs(pId,contractInputs);
                                
                                int count = 0;
                                while(count < 1000) {
                                    def comment = new java.lang.StringBuilder()
                                    if( count % 2 ){
                                        comment.append("Lorem Elsass ipsum vielmols, libero. amet, turpis Salu bissame sit Pellentesque Racing. rucksack salu tellus bissame réchime leo Huguette in,")
                                        comment.append("Roberstau commodo jetz gehts los hopla ullamcorper picon bière Verdammi id, leverwurscht consectetur ac rossbolla lotto-owe ornare porta sed suspendisse")
                                        comment.append("pellentesque Coopé de Truchtersheim non condimentum gewurztraminer")
                                        apiAccessor.processAPI.addProcessCommentOnBehalfOfUser(processInstance.id, comment.toString(), user.id)
                                    }else{
                                        comment.append("Wurschtsalad senectus ftomi!  DNA, id Chulia")
                                        apiAccessor.processAPI.addProcessComment(processInstance.id, comment.toString())
                                    }
                                
                                
                                    count++;
                                }
                                return "ok"
                                """.trimIndent().toScript(ExpressionConstants.API_ACCESSOR.toExpression(), "targetProcessName".toParameter(), "targetProcessVersion".toParameter())))

                        }
                        addTransition("start", "task1")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("walter.bates")
                })
            }

            setParameters(mapOf(
                    "targetProcessName" to targetProcessName,
                    "targetProcessVersion" to targetProcessVersion
            ))
        }



    }

    override fun accept(client: APIClient) {
        super.accept(client)
//        client.safeExec {
//            val formMapping = processAPI.searchFormMappings(SearchOptionsBuilder(0, 10)
//                    .filter(FormMappingSearchDescriptor.TYPE, FormMappingType.PROCESS_START)
//                    .filter(FormMappingSearchDescriptor.PROCESS_DEFINITION_ID, processDefinitionId).done()).result[0]
//            val processInstantiationForm = customPageAPI.getPageByName("custompage_processAutogeneratedForm")
//            processAPI.updateFormMapping(formMapping.id, null, processInstantiationForm.id)
//        }
    }
}
