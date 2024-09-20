package pt.isel

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpFilter
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.Logger
import org.slf4j.LoggerFactory

/**
 * The servlet API (interfaces and base classes) were initially defined by [Java Enterprise Edition]
 * (https://www.oracle.com/java/technologies/java-ee-glance.html).
 * They are now maintained by project [Jakarta](https://projects.eclipse.org/projects/ee4j)
 *
 */

/**
 * A servlet is responsible for ultimately handling an HTTP request by populating an [HttpServletResponse]
 * using information from a [HttpServletRequest].
 */
class ExampleServet : HttpServlet() {

    override fun doGet(
        request: HttpServletRequest,
        response: HttpServletResponse,
    ) {
        log.info("doGet: method='{}', uri='{}'",
            request.method,
            request.requestURI)

        response.status = 200
        // QUESTION what happens if 'charset=utf-8' is removed
        // QUESTION what happens if 'Content-Type' is set to 'application/json'
        response.addHeader("Content-Type", "text/plain; charset=utf-8")
        response.outputStream.apply {
            write("Olá mundo".toByteArray(Charsets.UTF_8))
            flush()
        }
        log.info("doGet: ending")
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ExampleServet::class.java)
    }
}

/**
 * A servlet filter contributes to the handling of HTTP responses, by using [HttpServletRequest]
 * and eventually mutating [HttpServletResponse] *before* and *after* the request is handled by a server.
 * Multiple filters are organized in a pipeline.
 */

class ExampleFilter : HttpFilter() {
    override fun doFilter(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain) {

        // Performed before the request is handled by the servlet
        val start = System.nanoTime()
        log.info("doFilter: before chain call")

        // Call the rest of the pipeline
        // QUESTION what happens if a filter does not call chain.doFilter?
        chain.doFilter(request, response)

        // Performed after the request was handled by the servlet
        val end = System.nanoTime()
        val delta = end - start
        log.info("doFilter: after chain call, took: {} us", delta)
    }

    companion object {
        private val log: Logger = LoggerFactory.getLogger(ExampleFilter::class.java)
    }
}