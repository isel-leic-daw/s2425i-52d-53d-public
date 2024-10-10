package pt.isel

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.model.Problem
import pt.isel.model.UserInput

@RestController
@RequestMapping("/api/users")
class UserController(
    private val userService: UserService,
) {
    @PostMapping
    fun createParticipant(
        @RequestBody participantInput: UserInput,
    ): ResponseEntity<*> {
        val result: Either<UserError, User> =
            userService
                .createUser(participantInput.name, participantInput.email)

        return when (result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure ->
                when (result.value) {
                    is UserError.AlreadyUsedEmailAddress ->
                        Problem.EmailAlreadyInUse.response(
                            HttpStatus.CONFLICT,
                        )
                }
        }
    }
}
