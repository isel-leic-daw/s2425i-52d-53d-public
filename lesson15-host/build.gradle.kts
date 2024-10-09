plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}


group = "pt.isel"
version = "0.1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Module dependencies
    implementation(project(":lesson12-agendify-http-api"))
    implementation(project(":lesson13-agendify-repository-jdbi"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // for JDBI and Postgres
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")

    testImplementation(kotlin("test"))
}

tasks.bootRun {
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
}
tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}