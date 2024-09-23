package pt.isel

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

data class AccountDataModel (
    val customer: String,
    val id: String,
    val balance: Long

)

@RestController
@RequestMapping("/msg-converter")
class ControllerMsgConverter {

    /**
     * An instance of a class is converted in JSON by default.
     * And header Content-Type according.
     * try:
     *    curl http://localhost:8080/msg-converter/path0
     */
    @GetMapping("/path0")
    fun handler0(): AccountDataModel {
        return AccountDataModel("Ze Manel", "be37dqbk", 1000)
    }
    /*
     * try:
     *    curl 'http://localhost:8080/msg-converter/path1?url=https://github.com/'
     */
    @GetMapping("/path1")
    fun handler1(@RequestParam url: String): URI {
        return URI(url)
    }

}