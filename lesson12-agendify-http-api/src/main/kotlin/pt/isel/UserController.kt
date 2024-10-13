package pt.isel

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import pt.isel.model.Problem
import pt.isel.model.UserCreateTokenInputModel
import pt.isel.model.UserCreateTokenOutputModel
import pt.isel.model.UserHomeOutputModel
import pt.isel.model.UserInput

@RestController
class UserController(
    private val userService: UserService,
) {
    @PostMapping("/api/users")
    fun createUser(
        @RequestBody userInput: UserInput,
    ): ResponseEntity<*> {
        val result: Either<UserError, User> =
            userService
                .createUser(userInput.name, userInput.email, userInput.password)

        return when (result) {
            is Success ->
                ResponseEntity
                    .status(HttpStatus.CREATED)
                    .header(
                        "Location",
                        "/api/users/${result.value.id}",
                    ).build<Unit>()

            is Failure ->
                when (result.value) {
                    is UserError.AlreadyUsedEmailAddress ->
                        Problem.EmailAlreadyInUse.response(
                            HttpStatus.CONFLICT,
                        )

                    UserError.InsecurePassword ->
                        Problem.InsecurePassword.response(
                            HttpStatus.BAD_REQUEST,
                        )
                }
        }
    }

    @PostMapping("/api/users/token")
    fun token(
        @RequestBody input: UserCreateTokenInputModel,
    ): ResponseEntity<*> {
        val res = userService.createToken(input.email, input.password)
        return when (res) {
            is Success ->
                ResponseEntity
                    .status(200)
                    .body(UserCreateTokenOutputModel(res.value.tokenValue))

            is Failure ->
                when (res.value) {
                    TokenCreationError.UserOrPasswordAreInvalid ->
                        Problem.UserOrPasswordAreInvalid.response(HttpStatus.BAD_REQUEST)
                }
        }
    }

    @PostMapping("api/logout")
    fun logout(user: AuthenticatedUser) {
        userService.revokeToken(user.token)
    }

    @GetMapping("/api/me")
    fun userHome(userAuthenticatedUser: AuthenticatedUser): UserHomeOutputModel =
        UserHomeOutputModel(
            id = userAuthenticatedUser.user.id,
            name = userAuthenticatedUser.user.name,
            email = userAuthenticatedUser.user.email,
        )
}
