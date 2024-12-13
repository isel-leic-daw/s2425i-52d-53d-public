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
    implementation(project(":lesson16-http-pipeline"))
    implementation(project(":lesson18-agendify-SSE"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // for JDBI and Postgres
    implementation("org.jdbi:jdbi3-core:3.37.1")
    implementation("org.postgresql:postgresql:42.7.2")

    testImplementation(kotlin("test"))
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-webflux")
}

tasks.bootRun {
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
}

tasks.withType<Test> {
    useJUnitPlatform()
    environment("DB_URL", "jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
    dependsOn(":lesson13-agendify-repository-jdbi:dbTestsWait")
    finalizedBy(":lesson13-agendify-repository-jdbi:dbTestsDown")
}
kotlin {
    jvmToolchain(21)
}
/**
 * Docker related tasks
 */

task<Copy>("extractUberJar") {
    dependsOn("assemble")
    // opens the JAR containing everything...
    from(zipTree(layout.buildDirectory.file("libs/lesson15-host-$version.jar").get().toString()))
    // ... into the 'build/dependency' folder
    into("build/dependency")
}

val dockerImageJvm = "agendify-jvm"
val dockerImageNginx = "agendify-nginx"
val dockerImagePostgresTest = "agendify-postgres-test"
val dockerImageUbuntu = "agendify-ubuntu"

task<Exec>("buildImageJvm") {
    dependsOn("extractUberJar")
    commandLine("docker", "build", "-t", dockerImageJvm, "-f", "test-infra/Dockerfile-jvm", ".")
}

task<Exec>("buildImageNginx") {
    commandLine("docker", "build", "-t", dockerImageNginx, "-f", "test-infra/Dockerfile-nginx", ".")
}

task<Exec>("buildImagePostgresTest") {
    commandLine(
        "docker",
        "build",
        "-t",
        dockerImagePostgresTest,
        "-f",
        "test-infra/Dockerfile-postgres-test",
        "../lesson13-agendify-repository-jdbi",
    )
}

task<Exec>("buildImageUbuntu") {
    commandLine("docker", "build", "-t", dockerImageUbuntu, "-f", "test-infra/Dockerfile-ubuntu", ".")
}

task("buildImageAll") {
    dependsOn("buildImageJvm")
    dependsOn("buildImageNginx")
    dependsOn("buildImagePostgresTest")
    dependsOn("buildImageUbuntu")
}

task<Exec>("allUp") {
    commandLine("docker", "compose", "up", "--force-recreate", "-d")
}

task<Exec>("allDown") {
    commandLine("docker", "compose", "down")
}
