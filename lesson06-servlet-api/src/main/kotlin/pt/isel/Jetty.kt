package pt.isel

import jakarta.servlet.DispatcherType
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.server.ServerConnector
import org.eclipse.jetty.servlet.FilterHolder
import org.eclipse.jetty.servlet.ServletContextHandler
import org.eclipse.jetty.servlet.ServletHolder
import org.slf4j.LoggerFactory
import java.util.*

private val log = LoggerFactory.getLogger("main")

/**
 * Creating a server using the Jetty library.
 * Based on based on:
 * [https://www.eclipse.org/jetty/documentation/jetty-11/programming-guide/index.html#pg-server-http-handler-use-servlet]
 */
fun main() {
    log.info("started")
    val server = Server(8080).apply {
        addConnector(ServerConnector(this))
        handler = ServletContextHandler().apply {
            contextPath = "/"
            addFilter(FilterHolder(ExampleFilter()), "/*", EnumSet.of(DispatcherType.REQUEST))
            addServlet(ServletHolder(ExampleServet()), "/*")
        }
    }
    server.start()
    server.join()
}