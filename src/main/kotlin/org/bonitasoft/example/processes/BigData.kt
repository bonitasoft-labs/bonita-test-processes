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

import org.bonitasoft.engine.bpm.bar.BarResource
import org.bonitasoft.engine.bpm.bar.BusinessArchiveBuilder
import org.bonitasoft.engine.bpm.bar.actorMapping.Actor
import org.bonitasoft.engine.bpm.bar.actorMapping.ActorMapping
import org.bonitasoft.engine.bpm.connector.ConnectorEvent
import org.bonitasoft.engine.bpm.contract.FileInputValue
import org.bonitasoft.engine.bpm.flownode.TaskPriority
import org.bonitasoft.engine.bpm.process.impl.ProcessDefinitionBuilder
import org.bonitasoft.engine.expression.ExpressionBuilder
import org.bonitasoft.engine.operation.OperationBuilder
import org.bonitasoft.example.toExpression
import org.bonitasoft.example.toScript
import java.io.BufferedOutputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.nio.file.Files
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarOutputStream
import javax.tools.ToolProvider

class BigData(private val number: Int) : BonitaProcess() {
    override fun process(): ProcessDefinitionBuilder =
            ProcessDefinitionBuilder().createNewInstance("BigData-$number", "1.0")
                    .apply {
                        addActor("theActor", true)
                        addStartEvent("start")

                        for (i in 1 until 15) {
                            addShortTextData("simpleVar" + i, "simple var content $i".toExpression())
                        }

                        addBusinessData("myEmployee","com.company.model.Employee", """
                            def employee = new com.company.model.Employee()
                            employee.name = "name"
                            return employee
                        """.trimIndent().toScript("com.company.model.Employee"))

                        addContextEntry("myEmployee_ref", ExpressionBuilder().createBusinessDataReferenceExpression("myEmployee"))

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

                        addXMLData("xmlVar1", """ 
                           <?xml version="1.0" encoding="utf-8"?>
                            <xs:schema targetNamespace="http://tempuri.org/XMLSchema.xsd"
                            
                                elementFormDefault="qualified"  
                            
                                xmlns="http://tempuri.org/XMLSchema.xsd" 
                            
                                xmlns:mstns="http://tempuri.org/XMLSchema.xsd"  
                            
                                xmlns:xs="http://www.w3.org/2001/XMLSchema">
                              <xs:simpleType name="AuthorInfor">
                                <xs:annotation>
                                  <xs:documentation>this element will all authors for book</xs:documentation>
                                </xs:annotation>
                                <xs:restriction base='xs:string'>
                                  <xs:maxLength value='15'/>
                                </xs:restriction>
                              </xs:simpleType>
                              <xs:simpleType name="IsdnInfo">
                                <xs:annotation>
                                  <xs:documentation>this element defines 10 digit ISDN code</xs:documentation>
                                </xs:annotation>
                                <xs:restriction base='xs:string'>
                                  <xs:maxLength value='10'/>
                                </xs:restriction>
                              </xs:simpleType>
                              <xs:complexType name="BookShelfInfo">
                                <xs:sequence>
                                  <xs:choice minOccurs="1" maxOccurs="1">
                                    <xs:choice minOccurs="1" maxOccurs="5">
                                      <xs:element name="byAuthor" type="AuthorInfor"/>
                                    </xs:choice>
                                    <xs:element name="byISDNNo" type="IsdnInfo"/>
                                  </xs:choice>
                                </xs:sequence>
                              </xs:complexType>
                              <xs:element name="MyBookShelf" type="BookShelfInfo"/>
                            </xs:schema>
                        """.trimIndent().toExpression())

                        addXMLData("xmlVar2", """
                            <?xml version="1.0" encoding="utf-8"?>
                            <MyBookShelf xmlns="http://tempuri.org/XMLSchema.xsd">
                              <byAuthor>Chetan Bhagat</byAuthor>
                              <byAuthor>Aditya Ghosh</byAuthor>
                              <byAuthor>Reena Mehta</byAuthor>
                            </MyBookShelf>
                        """.trimIndent().toExpression())
                        addContract().apply {
                            addFileInput("fileInputValues", "create my list of document", true)
                            addFileInput("file1", "single document")
                            addFileInput("file2", "single document")
                        }
                        addDocumentListDefinition("myDocumentList")
                                .addInitialValue(
                                        ExpressionBuilder().createContractInputExpression("fileInputValues", List::class.java.name))

                        addDocumentDefinition("myDoc").addInitialValue(ExpressionBuilder().createContractInputExpression("file1", FileInputValue::class.java.name))
                        addDocumentDefinition("myDoc2").addInitialValue(ExpressionBuilder().createContractInputExpression("file2", FileInputValue::class.java.name))
                        (1..10).forEach { index ->
                            addUserTask("taskWithPriorityUNDER_NORMAL-$index", "theActor").addPriority(TaskPriority.UNDER_NORMAL.name)
                            addUserTask("taskWithPriorityABOVE_NORMAL-$index", "theActor").addPriority(TaskPriority.ABOVE_NORMAL.name)
                            addUserTask("taskWithPriorityHIGHEST-$index", "theActor").addPriority(TaskPriority.HIGHEST.name)
                            addUserTask("taskWithPriorityLOWEST-$index", "theActor").addPriority(TaskPriority.LOWEST.name)

                        }
                        addUserTask("user1", "theActor").addPriority(TaskPriority.UNDER_NORMAL.name).apply {
                            addContract().addFileInput("fileInputValues", "update my list of document", true)
                            addOperation(OperationBuilder().createSetDocumentList("myDocumentList",
                                    ExpressionBuilder().createContractInputExpression("fileInputValues", List::class.java.name)))

                        }
                        addUserTask("user2", "theActor").addPriority(TaskPriority.ABOVE_NORMAL.name).addDisplayName("User 2".toExpression())
                        addUserTask("taskWithConnectors", "theActor").addPriority(TaskPriority.HIGHEST.name).apply {
                            addConnector("connectorThatSucceed1", "connectorThatSucceed", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorThatSucceed2", "connectorThatSucceed", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorThatSucceed3", "connectorThatSucceed", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorThatSucceed4", "connectorThatSucceed", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorThatSucceed5", "connectorThatSucceed", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorToBeSkipped1", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorToBeSkipped2", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorToBeSkipped3", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorToBeSkipped4", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorToBeSkipped5", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER)
                            addConnector("connectorThatFails1", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER).ignoreError()
                            addConnector("connectorThatFails2", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER).ignoreError()
                            addConnector("connectorThatFails3", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER).ignoreError()
                            addConnector("connectorThatFails4", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER).ignoreError()
                            addConnector("connectorThatFails5", "connectorThatFails", "1.0", ConnectorEvent.ON_ENTER)// we will be in that state
                            addConnector("connectorToBeExecuted1", "connectorThatSucceed", "1.0", ConnectorEvent.ON_FINISH)
                            addConnector("connectorToBeExecuted2", "connectorThatSucceed", "1.0", ConnectorEvent.ON_FINISH)
                            addConnector("connectorToBeExecuted3", "connectorThatSucceed", "1.0", ConnectorEvent.ON_FINISH)
                            addConnector("connectorToBeExecuted4", "connectorThatSucceed", "1.0", ConnectorEvent.ON_FINISH)
                            addConnector("connectorToBeExecuted5", "connectorThatSucceed", "1.0", ConnectorEvent.ON_FINISH)
                        }
                        addAutomaticTask("user3").addDisplayName("User 3".toExpression())
                        addAutomaticTask("userTaskFailed").addDisplayName("throw new Exception()".toScript())
                        addTransition("start", "user1")
                        addTransition("start", "user2")
                        addTransition("start", "user3")
                        addTransition("start", "taskWithConnectors")
                    }

    override fun withResources(bar: BusinessArchiveBuilder) {
        bar.apply {
            actorMapping = ActorMapping().apply {
                addActor(Actor("theActor").apply {
                    addUser("jean.némar")
                    addUser("walter.bates")
                })
            }
            addConnectorImplementation(generateImpl("connectorThatSucceed", "implementationOfConnectorThatSucceed", "ConnectorThatSucceed"))
            addClasspathResource(BarResource("connectorThatSucceed.jar", generateJar("ConnectorThatSucceed", """
                public class ConnectorThatSucceed extends org.bonitasoft.engine.connector.AbstractConnector {
                    public void validateInputParameters() {}
                    protected void executeBusinessLogic() {}
                }
""".trimIndent())))

            addConnectorImplementation(generateImpl("connectorThatFails", "implementationOfConnectorThatFails", "ConnectorThatFails"))
            addClasspathResource(BarResource("connectorThatFails.jar", generateJar("ConnectorThatFails", """
                public class ConnectorThatFails extends org.bonitasoft.engine.connector.AbstractConnector {
                    public void validateInputParameters() {}
                    protected void executeBusinessLogic() { throw new java.lang.RuntimeException("exception thrown by the connector");}
                }
""".trimIndent())))
        }
    }

    private fun generateImpl(connectorId: String, implementationId: String, connectorClass: String): BarResource {
        return BarResource("$connectorId.impl", """
                    <connectorImplementation>
                        <definitionId>$connectorId</definitionId>
                        <definitionVersion>1.0</definitionVersion>
                        <implementationClassname>$connectorClass</implementationClassname>
                        <implementationId>$implementationId</implementationId>
                        <implementationVersion>1.0</implementationVersion>
                    <jarDependencies>
                        <jarDependency>${connectorId}.jar</jarDependency>
                    </jarDependencies>
                    </connectorImplementation>
                    """.trimIndent().toByteArray()
        )
    }

    private fun generateJar(className: String, content: String): ByteArray? {
        val root = Files.createTempDirectory("tempCompile").toFile()
        val sourceFile = File(root, "$className.java")
        Files.write(sourceFile.toPath(), content.toByteArray())
        val compiler = ToolProvider.getSystemJavaCompiler()
        val run = compiler.run(null, null, null, sourceFile.path)
        require(run == 0) { "Unable to compile the file, see logs" }
        val bytes = Files.readAllBytes(root.toPath().resolve("$className.class"))
        return generateJar(Collections.singletonMap("$className.class", bytes))
    }

    private fun generateJar(resources: Map<String, ByteArray>): ByteArray {
        var baos: ByteArrayOutputStream? = null
        var jarOutStream: JarOutputStream? = null
        try {
            baos = ByteArrayOutputStream()
            jarOutStream = JarOutputStream(BufferedOutputStream(baos))
            for ((key, value) in resources) {
                jarOutStream.putNextEntry(JarEntry(key))
                jarOutStream.write(value)
            }
            jarOutStream.flush()
            baos.flush()
        } finally {
            jarOutStream?.close()
            baos?.close()
        }
        return baos!!.toByteArray()
    }

}
