package pt.isel

import org.springframework.web.bind.annotation.*

data class StudentInputModel(
    val name: String,
    val nr: Int,
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
    fun handler2BodyParser(@RequestBody std: StudentInputModel): String {
        return "Request to path 2 with student: $std"
    }

}