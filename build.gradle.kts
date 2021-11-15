import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.4.20")
    id("com.bonitasoft.gradle.bonita-formatting").version("0.1.53")
    id("org.jetbrains.kotlin.kapt").version("1.5.31")
    application
}

repositories {
    jcenter()
    mavenLocal()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.bonitasoft.engine:bonita-client:7.12.1")
    implementation("com.github.javafaker:javafaker:1.0.2")
    implementation("org.apache.logging.log4j:log4j-core:2.14.0")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.14.0")
    implementation("org.awaitility:awaitility-kotlin:4.0.3")
    implementation("org.nield:kotlin-statistics:1.2.1")
    implementation("info.picocli:picocli:4.6.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
    kapt("info.picocli:picocli-codegen:4.6.2")
}

kapt {
    arguments {
        arg("project", "${project.group}/${project.name}")
        arg("disable.proxy.config")
        arg("disable.reflect.config")
        arg("disable.resource.config")
    }
}

application {
    mainClassName = "org.bonitasoft.example.AppKt"
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
