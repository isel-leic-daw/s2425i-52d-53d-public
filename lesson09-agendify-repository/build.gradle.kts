plugins {
    kotlin("jvm") version "1.9.25"
}

group = "pt.isel"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lesson09-agendify-domain"))

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}