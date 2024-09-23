package pt.isel

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size
import org.springframework.web.bind.annotation.*

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

// See https://beanvalidation.org for the Length and Min annotations.

data class StudentInputModel(
    @get:Size(min = 0, max = 10) val name: String,
    val nr: Int,
    @get:Min(1999)
    val year: Int
)

@RestController
class ControllerArgParser {
    /**
     * Example of a route with query-string parameters
     * Try with:
     *     curl 'http://localhost:8080/path0?nr=17'
     * Argument parser is responsible for converting the String to Int.
     * NOTE: use Nullable for optional qs parameters
     */
    @GetMapping("/path0")
    fun handler0QsParser(@RequestParam nr: Int?): String {
        return "Request to path 0 with argument: $nr"
    }
    /**
     * Example of a route with path parameter
     * Try with:
     *     curl 'http://localhost:8080/path1/17'
     * Argument parser is responsible for converting the String to Int.
     */
    @GetMapping("/path1/{nr}")
    fun handler1RoutePathParamParser(@PathVariable nr: Int): String {
        return "Request to path 1 with path parameter: $nr"
    }
    /**
     * Try with:
        curl -H "Content-Type: application/json" \
             -d '{"name":"Ze Rambo","nr":"87236", "year":2019}' \
             http://localhost:8080/path2
     */
    @PostMapping("/path2")
    fun handler2BodyParser(@Valid @RequestBody std: StudentInputModel): String {
        return "Request to path 2 with student: $std"
    }
    /**
     * Access all information in HTTP request
     */
    @GetMapping("/path3")
    fun handler3(req: HttpServletRequest) {

    }

}