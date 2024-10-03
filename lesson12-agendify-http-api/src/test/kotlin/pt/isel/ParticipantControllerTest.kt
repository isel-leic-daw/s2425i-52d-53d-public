package pt.isel

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.model.ParticipantInput

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ParticipantControllerTest {

    // Injected by the test environment
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var trxManager: TransactionManager

    val johnDoe = ParticipantInput(
        name = "John Doe",
        email = "john.doe@example.com",
        kind = ParticipantKind.GUEST
    )

    @BeforeAll
    fun setup() {
        trxManager.run {
            repoParticipants.createParticipant(
                johnDoe.name,
                johnDoe.email,
                johnDoe.kind
            )
        }

    }

    @Test
    fun `should create a participant and return 201 status`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // and: a random participant
        val rose = ParticipantInput(name = "Rose Mary", email = "rose@example.com", kind = ParticipantKind.GUEST)

        // Perform the request and assert the results
        client.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(rose)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("name").isEqualTo("Rose Mary")
            .jsonPath("email").isEqualTo("rose@example.com")
            .jsonPath("kind").isEqualTo(ParticipantKind.GUEST.name)
    }

    @Test
    fun `should return 409 when email is already in use`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform the request and assert the results
        client.post()
            .uri("/participants")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(johnDoe)
            .exchange()
            .expectStatus().isEqualTo(409)
            .expectHeader().contentType(MediaType.APPLICATION_PROBLEM_JSON)
            .expectBody()
            .jsonPath("title").isEqualTo("email-already-in-use")
    }
}
