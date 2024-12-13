package pt.isel

import org.slf4j.LoggerFactory
import org.springframework.util.StopWatch
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse.BodyHandlers

private val logger = LoggerFactory.getLogger("main")

fun main() {
    val httpClient = HttpClient.newHttpClient()
    val request = HttpRequest.newBuilder(URI.create("http://localhost:8088/api/status/ip")).GET().build()
    while (true) {
        val sw = StopWatch().also { it.start() }
        logger.info("Request sent...")
        val response = httpClient.send(request, BodyHandlers.ofString())
        sw.stop()
        logger.info("...response received: {}, {}, {}", response.statusCode(), sw.totalTimeMillis, response.body())
        Thread.sleep(2000)
    }
}