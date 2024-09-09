= Introduction to the Spring Framework

== Creating an (almost) empty JVM project with Spring Boot

* Got to https://start.spring.io/
** Select
*** Gradle project (the same build system we used on the LS course), using the kotlin language for the build script.
**** Q: what is a build script?
*** Kotlin language.
*** The latest stable version of Spring Boot.
*** JAR packaging.
*** Java version 21.
** Define the project metadata, such as group, artifact, ...
** Also select the following dependencies:
*** `Spring Web` (NOT `Spring Reactive Web`).

* Finally, select `Generate`. This will produce a compressed archive.

* Uncompress the archive file into a folder.

* Inspect the folder, noticing:
** The presence of a `gradlew` file indicates that this is a gradle based project.
** The presence of a `build.gradle.kts` containing:
*** The plugins, namely: `kotlin` and others that are specific to Spring (these will add new build tasks).
*** The required dependencies:
**** The Spring Boot library - `org.springframework.boot:spring-boot-starter-web`.
**** The [Jackson library](https://github.com/FasterXML/jackson), which will handle JSON serialization and deserialization.
**** The Kotlin reflection library.
**** The Spring Boot library for tests - `org.springframework.boot:spring-boot-starter-test`.
**** Q: what is the difference between `implementation` and `testImplementation` on the `dependencies` block?

* Start Intellij (Community Edition is enough), open the `build.gradle.kts` file as a project, and browse around.
** There is a single file under the `main` folder - `DemoApplication.kt` (or similar).
*** It has an empty class `DemoApplication` (or similar), annotated with `@SpringBootApplication`
**** It also has a main function that simply calls `runApplication<DemoApplication>(*args)>`, using the above class as the generic argument.

* Notice how the project defines a rather simple console application with a `main` entry point.

== Building and running the application

* Run `./gradlew build`
** There will be at least one JAR inside `build/libs`
** One of those JARs contains all the classes required to run the application, including the third-party dependencies, such as the Spring libraries. No other dependency will be needed for that.
*** Uncompress the JAR and take a look around. Notice `.class` files originating on the project's source code and on the dependencies.
*** It is usual to name this JAR as the "uber JAR" or a "fat JAR".

* Run the application by doing `java -jar build/libs/<library-name>.jar`.
** Notice the following in the output - `Tomcat started on port(s): 8080 (http) with context path ''`
*** Tomcat is a servlet server, similar to Jetty (which we used in LS). 
*** The server will use port 8080. 
*** The `context path` is `''` meaning that all application paths will start from the root.

* To stop the application, do a `Control-C`
** Notice how there is a shutdown process, visible in the `Shutting down ExecutorService 'applicationTaskExecutor'` message.

== Adding HTTP request handlers

* The project that we configured uses a library called `Spring MVC` to handle HTTP requests.
** `MVC` comes from Model-View-Controller term.

* A way to define HTTP request handlers is by defining methods inside a _controller_ class.
** I.e. a controller is a class that contains request handing methods (i.e. _handlers_)

* Create an `ExampleController` class
** Annotate it with `@RestController`
*** This _mark_ this class as being a controller to the Spring framework.
*** Notice that there isn't a required base class or implemented interface.
*** The meaning of `Rest` in `RestController` is be discussed later on. 

* Inside this class, create a method that returns the `Hello Web` string and annotate it with `@GetMapping("/examples/1")`.
** This annotation defines the mapping between the request's HTTP method and target URI path and the class function.

----
@GetMapping("/examples/1")
fun get() = "Hello Web"
----

* Start the application and do a request to `http://locahost:8080/examples/1`
** Notice that the response contains the `Hello Web` string in the body.
** Do the request using curl (e.g. `curl -i http://localhost:8080/examples/1`) and notice that the `Content-Type` header is `text/plain;charset=UTF-8`.
** Do the request from a browser and notice that the `Content-Type` is now `text/html; charset=UTF-8`.
*** Q: Do you know how why the response's content type is different? Can you change the `curl` request commands tso hat the response also uses the `text/html` media type.

* Break the execution inside an handler function and observe the call stack.

== Controller life-cycle

* By using logging statements and by performing multiple HTTP requests:
** Observe how many `ExampleController` instances are created.
** Observe the identifiers of the threads where the handler methods are called.

* Q: given the above observations, what should be the restrictions to the instance state?

== Dependencies, inversion of control, and dependency injection

* Start by creating a interface that defines the functionality of a _service_, in this case a service responsible for computing a greeting message.

----
interface GreetingsService {
    val greeting: String
}
----

* Then, create a simple implementation of that service, returning an hard-code message.
** The meaning of the `@Component` annotation will be discussed afterwards.

----
@Component
class DefaultGreetingService : GreetingsService {
    override val greeting: String = "Hello DAW"
}
----

* Finally, have the `ExampleController` receive a `GreetingService` instance on the constructor and use it on the handler method.

----
@RestController
class ExampleController(
    private val greetingsService: GreetingsService,
) {

    @GetMapping("/examples/1")
    fun getHello() = greetingsService.greeting
}
----

* Restart the application, do a `GET` request to `http://localhost:8080/example/1` and observe the result.

* There are some interesting things going on here:
** _Dependency_ concept: the `GreetingsService` is a dependency of the `ExampleController`, i.e., the `ExampleController` needs a `GreetingsService` to do its job.

** _Inversion of Control_ concept: The `ExampleController` does *not* instantiate the dependency. Instead, it receives the dependency as a constructor parameter.
*** We call it _inversion_ because the user of the dependency (i.e. the `ExampleController`) receives that dependency instead of creating it.
*** This provides an interesting _independence_, also called _decoupling_, between the user of a functionality and the implementation of that functionality.
**** `ExampleController` only depends on the `GreetingService` (an interface) and is not aware of any of its implementations.
**** `ExampleController` does not know how to instantiate or obtain a `GreetingService`. It just states that it depends on a `GreetingService` by having a constructor parameter of that type.

** _Injection_ concept: the dependency is provided to (i.e. injected to) the instance that needs the dependency.
*** The dependency is provided via the constructor, so we call this _constructor injection_.
*** Constructor injection fits really well in the object-oriented programming model, where constructors should leave instance in a valid state. Since `ExampleController` requires a `GreetingService` in order to do its job, then an instance _valid state_ requires a `GreetingService`.

* _Dependency Graph_ 
** In the above case we have a really simple example of a _dependency graph_:
*** The graph vertexes are the instances.
**** I.e. `ExampleController` and `GreetingsService` instances are the vertexes in this example.
*** The graph edges are the dependency relations.
**** I.e. The dependency between `ExampleController` and `GreetingsServices` is the edge.

** A non-example application has typically much more complex graph.

** The creation of the dependency graph is typically called as _composition_.

=== Container or context

* Where and when are the `ExampleController` and `DefaultGreetingService` instances created?
** Notice that until now we just defined classes and constructors. There isn't any instance creation in the application code.

* The instantiation is performed by a so called _dependency injection container_. 

* The Spring Framework calls it a _context_ and calls the instances managed by this context as _beans_.

* How does a container/context determines the dependency relations?
** By introspecting the class constructors?

* How does the container/context determines the classes to instantiate?
** By scanning a subset of the classpath for classes annotated with special annotations, such as `@Component`.

