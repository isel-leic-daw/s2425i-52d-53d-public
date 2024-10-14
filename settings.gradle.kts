plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
    kotlin("jvm") version "1.9.25" apply false
    kotlin("plugin.spring") version "1.9.25" apply false
    id("org.springframework.boot") version "3.3.3" apply false
    id("io.spring.dependency-management") version "1.1.6" apply false
}
rootProject.name = "daw-2024-i52-i53"

// include("lesson02-intro-spring")
include("lesson03-beans-on-spring")
include("lesson04-ioc-and-di")
include("lesson05-ioc-and-di-container")
include("lesson05-ioc-and-di-on-spring")
include("lesson06-servlet-api")
include("lesson07-spring-web-pipeline")
include("lesson09-agendify-domain")
include("lesson09-agendify-repository")
include("lesson09-agendify-services")
include("lesson12-agendify-http-api")
include("lesson13-agendify-repository-jdbi")
include("lesson15-host")
include("lesson16-http-pipeline")
