import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm").version("1.3.50")
    id("com.bonitasoft.gradle.bonita-formatting").version("0.1.53")
    application
}

repositories {
    jcenter()
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.bonitasoft.engine:bonita-client:7.10.5")
    implementation("com.bonitasoft.engine:bonita-client-sp:7.10.5")
    implementation("org.bonitasoft.web:bonita-java-client:0.0.1")
    implementation("com.github.javafaker:javafaker:1.0.2")
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit")
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
