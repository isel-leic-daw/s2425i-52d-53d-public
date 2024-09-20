package pt.isel

import org.apache.catalina.connector.Connector
import org.apache.catalina.startup.Tomcat
import org.apache.tomcat.util.descriptor.web.FilterDef
import org.apache.tomcat.util.descriptor.web.FilterMap
import org.slf4j.LoggerFactory
import java.io.File

private val log = LoggerFactory.getLogger("main")

/**
 * Creating a server using the Tomcat library.
 * Based on based on:
 * [https://www.eclipse.org/jetty/documentation/jetty-11/programming-guide/index.html#pg-server-http-handler-use-servlet]
 */
const val FILTER_NAME = "example-filter"
const val SERVLET_NAME = "example-servlet"
fun main() {
    log.info("started")
    val tomcat = Tomcat().apply {
        connector = Connector()
        connector.port = 8088
        val context = addContext("/", File(".").absolutePath)

        addServlet("/", SERVLET_NAME, ExampleServet())
        context.addServletMappingDecoded("/*", SERVLET_NAME)

        context.addFilterDef(FilterDef().apply {
            filter = ExampleFilter()
            filterName = FILTER_NAME
        })
        context.addFilterMap(FilterMap().apply{
            filterName = FILTER_NAME
            addURLPattern("/*")
        })
    }
    tomcat.start()
    tomcat.server.await()
}