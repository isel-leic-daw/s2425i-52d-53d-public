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

    // for JDBI
    testImplementation(project(":lesson13-agendify-repository-jdbi"))
    testImplementation("org.jdbi:jdbi3-core:3.37.1")
    testImplementation("org.postgresql:postgresql:42.7.2")

    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

tasks.test {
    useJUnitPlatform()
    dependsOn(":lesson13-agendify-repository-jdbi:dbTestsWait")
    finalizedBy(":lesson13-agendify-repository-jdbi:dbTestsDown")
}
kotlin {
    jvmToolchain(21)
}

/**
 * DB related tasks
 * - To run `psql` inside the container, do
 *      docker exec -ti db-tests psql -d db -U dbuser -W
 *   and provide it with the same password as define on `tests/Dockerfile-db-test`
 */

val composeFileDir: Directory = rootProject.layout.projectDirectory
val dockerComposePath = composeFileDir.file("docker-compose.yml").toString()

task<Exec>("dbTestsUp") {
    commandLine("docker", "compose", "-f", dockerComposePath, "up", "-d", "--build", "--force-recreate", "db-tests")
}

task<Exec>("dbTestsWait") {
    commandLine("docker", "exec", "db-tests", "/app/bin/wait-for-postgres.sh", "localhost")
    dependsOn("dbTestsUp")
}

task<Exec>("dbTestsDown") {
    commandLine("docker", "compose", "-f", dockerComposePath, "down", "db-tests")
}
