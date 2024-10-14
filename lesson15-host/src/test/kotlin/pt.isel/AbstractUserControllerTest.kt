package pt.isel

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.model.UserCreateTokenOutputModel
import pt.isel.model.UserInput
import kotlin.test.assertTrue

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractUserControllerTest {
    // Injected by the test environment
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var trxManager: TransactionManager

    val johnDoe =
        UserInput(
            name = "John Doe",
            email = "john.doe@example.com",
            password = "password",
        )

    @BeforeAll
    fun setup() {
        trxManager.run {
            repoParticipants.clear()
            repoSlots.clear()
            repoEvents.clear()
            repoUsers.clear()
            repoUsers.createUser(
                johnDoe.name,
                johnDoe.email,
                PasswordValidationInfo(newTokenValidationData()),
            )
        }
    }

    @Test
    fun `can create an user, obtain a token, and access user home, and logout`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a user
        val name = "John Rambo"
        val email = "john@rambo.vcom"
        val password = "badGuy"

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        client
            .post()
            .uri("/users")
            .bodyValue(
                mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to password,
                ),
            ).exchange()
            .expectStatus()
            .isCreated
            .expectHeader()
            .value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }

        // when: creating a token
        // then: the response is a 200
        val result =
            client
                .post()
                .uri("/users/token")
                .bodyValue(
                    mapOf(
                        "email" to email,
                        "password" to password,
                    ),
                ).exchange()
                .expectStatus()
                .isOk
                .expectBody(UserCreateTokenOutputModel::class.java)
                .returnResult()
                .responseBody!!

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        client
            .get()
            .uri("/me")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody()
            .jsonPath("email")
            .isEqualTo(email)
            .jsonPath("name")
            .isEqualTo(name)

        // when: getting the user home with an invalid token
        // then: the response is a 401 with the proper problem
        client
            .get()
            .uri("/me")
            .header("Authorization", "Bearer ${result.token}-invalid")
            .exchange()
            .expectStatus()
            .isUnauthorized
            .expectHeader()
            .valueEquals("WWW-Authenticate", "bearer")

        // when: revoking the token
        // then: response is a 200
        client
            .post()
            .uri("/logout")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus()
            .isOk

        // when: getting the user home with the revoked token
        // then: response is a 401
        client
            .get()
            .uri("/me")
            .header("Authorization", "Bearer ${result.token}")
            .exchange()
            .expectStatus()
            .isUnauthorized
            .expectHeader()
            .valueEquals("WWW-Authenticate", "bearer")
    }

    @Test
    fun `should create a participant and return 201 status`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random participant
        val rose = UserInput(name = "Rose Mary", email = "rose@example.com", "rainhaDoCaisSodre")

        // Perform the request and assert the results
        client
            .post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rose)
            .exchange()
            .expectStatus()
            .isCreated
            .expectHeader()
            .value("location") {
                assertTrue(it.startsWith("/api/users/"))
            }
    }

    @Test
    fun `should return 409 when email is already in use`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform the request and assert the results
        client
            .post()
            .uri("/users")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(johnDoe)
            .exchange()
            .expectStatus()
            .isEqualTo(409)
            .expectHeader()
            .contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("title")
            .isEqualTo("email-already-in-use")
    }
}
