package pt.isel.lesson02_spring_intro

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class ControllerExample {
    @GetMapping("/hello")
    fun handlerHello(): String {
        return "<html><h1>Hello World</h1></html>"
    }
}