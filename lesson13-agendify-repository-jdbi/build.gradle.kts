plugins {
    kotlin("jvm") version "1.9.25"
}

group = "pt.isel"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":lesson09-agendify-repository"))
    implementation(project(":lesson09-agendify-domain"))

    // for JDBI
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.jdbi:jdbi3-kotlin:3.37.1")
    implementation("org.jdbi:jdbi3-postgres:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}