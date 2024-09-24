package pt.isel

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.net.URI

data class AccountOutputModel (
    val id: String,
    val holder: String,
    val balance: Long
)

@RestController
@RequestMapping("msgs-converter")
class ControllerMsgsConverter {
    /**
     * Automatic conversion of objects in JSON.
     * try:
     *  curl 'http://localhost:8080/msgs-converter/path0'
     */
    @GetMapping("path0")
    fun handler0() : AccountOutputModel {
        return AccountOutputModel(
            "6853trgejd", "Maria Rosa", 1000
        )
    }
    /**
     * Custom conversion. E.g. URI -> PNG image with a QR Code
     * Client sends a URI and the handler responds with the QR Code for that URI.
     * E.g. curl 'http://localhost:8080/msgs-converter/path1?path=https://example.com'
     */
    @GetMapping("path1")
    fun handler1(@RequestParam path: String): URI {
        return URI(path)
    }
}