package pt.isel

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

private val logger = LoggerFactory.getLogger(ControllerExample::class.java)

@RestController
class ControllerExample(private val service: ServiceGreetings) {
    init {
        logger.info("##### INSTANTIATING controller!!!")
    }

    @GetMapping("/hello")
    fun handlerHello(): String {
        logger.info("##### Controller hash ${hashCode()} " +
            "on thread ${Thread.currentThread().threadId()}")
        // return "<html><h1>Hello World</h1></html>"
        logger.info("##### Service hash ${service.hashCode()}")
        return service.greeting
    }
}