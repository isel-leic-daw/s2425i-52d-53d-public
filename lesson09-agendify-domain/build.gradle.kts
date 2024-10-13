plugins {
    kotlin("jvm") version "1.9.25"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
}

group = "pt.isel"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // To use Named annotation for Dependency Injection
    implementation("jakarta.inject:jakarta.inject-api:2.0.1")

    // To get password encode
    api("org.springframework.security:spring-security-core:6.3.0")

    // To use Kotlin specific date and time functions
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")

    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
