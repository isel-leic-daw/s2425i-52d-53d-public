plugins {
    kotlin("jvm") version "1.9.25"
}

group = "pt.isel"
version = "0.1.0.SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":lesson09-agendify-repository"))
    api(project(":lesson09-agendify-domain"))

    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}