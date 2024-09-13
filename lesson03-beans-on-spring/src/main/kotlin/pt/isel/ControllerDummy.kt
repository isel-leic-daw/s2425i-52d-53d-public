package pt.isel

import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


private val logger = LoggerFactory.getLogger(ControllerDummy::class.java)

@RestController
class ControllerDummy(private val service: ServiceGreetings) {
    init {
        logger.info("##### INSTANTIATING controller!!!")
    }

    @GetMapping("/dummy")
    fun handlerDummy(): String {
        logger.info("##### Controller hash ${hashCode()} " +
            "on thread ${Thread.currentThread().threadId()}")
        logger.info("##### Service hash ${service.hashCode()} ")
        return service.greeting
    }
}