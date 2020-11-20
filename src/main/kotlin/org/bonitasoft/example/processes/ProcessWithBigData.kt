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
import org.bonitasoft.engine.bpm.contract.FileInputValue
import org.bonitasoft.engine.bpm.process.DesignProcessDefinition
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.toExpression
import org.bonitasoft.example.toScript

class ProcessWithBigData(private val number: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("BigData-$number", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addStartEvent("start")

                        addLongTextData("longText1", """
                            def value = new java.lang.StringBuilder()
                            for(int i = 0; i < 1000; i++) {
                                value.append("Lorem Elsass ipsum vielmols, libero. amet, turpis Salu bissame sit Pellentesque Racing. rucksack salu tellus bissame réchime leo Huguette in,")
                                value.append("Wurschtsalad senectus ftomi! mamsell semper Spätzle schpeck s'guelt elementum varius Gal. leo ante et Morbi kuglopf ac Pfourtz ! DNA, id Chulia")
                                value.append("Roberstau commodo jetz gehts los hopla ullamcorper picon bière Verdammi id, leverwurscht consectetur ac rossbolla lotto-owe ornare porta sed suspendisse")
                                value.append("pellentesque Coopé de Truchtersheim non condimentum gewurztraminer")
                            }
                            return value.toString()
                        """.trimIndent().toScript())

                        addContract().addFileInput("fileInputValues", "create my list of document", true)
                        addDocumentListDefinition("myDocumentList")
                                .addInitialValue(
                                        ExpressionBuilder().createContractInputExpression("fileInputValues", List::class.java.name))

                        addDocumentDefinition("myDoc").addInitialValue(ExpressionBuilder().createContractInputExpression("file1", FileInputValue::class.java.name))
                        addDocumentDefinition("myDoc2").addInitialValue(ExpressionBuilder().createContractInputExpression("file2", FileInputValue::class.java.name))

                        addUserTask("user1", "theActor").apply {
                            addContract().addFileInput("fileInputValues", "update my list of document", true)
                            addOperation(OperationBuilder().createSetDocumentList("myDocumentList",
                                    ExpressionBuilder().createContractInputExpression("fileInputValues", List::class.java.name)))
                        }

                        addUserTask("user2", "theActor").addDisplayName("User 2".toExpression())
                        addAutomaticTask("user3").addDisplayName("User 3".toExpression())

                        addTransition("start", "user1")
                        addTransition("start", "user2")
                        addTransition("start", "user3")
                    }


    /*
        //when
        final ProcessDefinition processDefinition = deployAndEnableProcessWithActor(builder.done(), ACTOR_NAME, matti);
        final ProcessInstance processInstance = getProcessAPI().startProcessWithInputs(processDefinition.getId(),
                Collections.<String, Serializable> singletonMap("reportInit",
                        new FileInputValue("theFile", "", "theContent".getBytes())));
        final HumanTaskInstance userTask = waitForUserTaskAndGetIt(TASK1);        //then
        final ContractDefinition contract = getProcessAPI().getUserTaskContract(userTask.getId());
        assertThat(contract.getInputs()).hasSize(2);
        final InputDefinition complexInput = contract.getInputs().get(0);
        assertThat(complexInput.getName()).isEqualTo("expenseLine");
        assertThat(complexInput.isMultiple()).as("should be multiple").isTrue();
        assertThat(complexInput.getDescription()).isEqualTo("expense report line");
        assertThat(complexInput.getInputs()).as("should have 3 inputs").hasSize(3);
        final InputDefinition fileInput = contract.getInputs().get(1);
        assertThat(fileInput.getInputs()).hasSize(2);
        assertThat(fileInput.getInputs()).containsExactly(new InputDefinitionImpl("filename", "Name of the file"),
                new InputDefinitionImpl("content", "Content of the file"));
        final Document myDoc = getProcessAPI().getLastDocument(processInstance.getId(), "myDoc");
        final byte[] documentContent = getProcessAPI().getDocumentContent(myDoc.getContentStorageId());
        assertThat(new String(documentContent)).isEqualTo("theContent");        //clean up
        disableAndDeleteProcess(processDefinition);
     */

    override fun withResources(bar: BusinessArchiveBuilder, processDefinition: DesignProcessDefinition) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("jean.némar")
                    addUser("walter.bates")
                })
            }
//            addClasspathResource(BarResource("jar$number.jar", ByteArray(2 * 1000 * 1000).apply { Random().nextBytes(this) }))
        }
    }
}
