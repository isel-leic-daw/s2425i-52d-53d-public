package pt.isel

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.net.InetAddress

@RestController
class StatusController {
    @GetMapping("/api/status/hostname")
    fun getStatusHostname(): String = System.getenv("HOSTNAME")

    @GetMapping("/api/status/ip")
    fun getStatusIp(): String = InetAddress.getLocalHost().hostAddress
}