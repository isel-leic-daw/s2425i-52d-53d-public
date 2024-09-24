package pt.isel

import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

data class StudentInputModel (
    val nr: Int,
    @get:Size(min=5, max=10) val name: String,
    @get:Min(1999) val year: Int
)
@RestController
class ControllerArgsResolverExample {

    /**
     * Parsing query string to method arguments.
     * Use Nullable type for optional parameters, or a default value.
     * Try:
     *     curl 'http://localhost:8080/path0?nr=11'
     */
    @GetMapping("path0")
    fun handler0(@RequestParam nr: Int = 23): String {
        return "Query string parameter nr = $nr"
    }

    /**
     * Parsing path parameters to method arguments.
     * Try:
     *     curl 'http://localhost:8080/path1/team/pistons/player/11'
     */
    @GetMapping("path1/team/{team}/player/{nr}")
    fun handler1(@PathVariable team: String, @PathVariable nr: Int): String {
        return "Path parameters team = $team and player = $nr"
    }

    /**
     * Automatic support of Spring using Jackson lib to convert JSON to an object.
     * try:
        curl -X POST \
             -H "Content-Type: application/json" \
             -d '{"name":"Ze Rambo","nr":"87236", "year":2019}' \
             http://localhost:8080/path2
     */
    @PostMapping("path2")
    fun handler2(@Valid @RequestBody std: StudentInputModel): String {
        return "Handler 2 called with: $std"
    }
}
