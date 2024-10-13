package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.model.EventInput
import kotlin.math.abs
import kotlin.random.Random

fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

@SpringBootTest(
    properties = ["spring.main.allow-bean-definition-overriding=true"],
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
)
abstract class AbstractEventControllerTest {
    // Injected by the test environment
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var trxManager: TransactionManager

    @BeforeEach
    fun setUp() {
        trxManager.run {
            repoParticipants.clear()
            repoSlots.clear()
            repoEvents.clear()
            repoUsers.clear()
        }
    }

    @Test
    fun `getAllEvents should return a list of events`() {
        val rose =
            trxManager.run {
                repoUsers.createUser(
                    "Rose Mary",
                    "rose@example.com",
                    PasswordValidationInfo(newTokenValidationData()),
                )
            }
        val event0 =
            trxManager.run { repoEvents.createEvent("Swim", "Swim for 2K free style", rose, SelectionType.SINGLE) }
        val event1 =
            trxManager.run {
                repoEvents.createEvent(
                    "Status Meeting",
                    "Coffee break and more",
                    rose,
                    SelectionType.MULTIPLE,
                )
            }

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform GET request and verify response
        client
            .get()
            .uri("/events")
            .exchange()
            .expectStatus()
            .isOk
            .expectBodyList(Event::class.java)
            .hasSize(2)
            .contains(event1, event0)
    }

    @Test
    fun `getEventById should return an event if found`() {
        val rose =
            trxManager.run {
                repoUsers.createUser(
                    "Rose Mary",
                    "rose@example.com",
                    PasswordValidationInfo(newTokenValidationData()),
                )
            }
        val event0 =
            trxManager.run { repoEvents.createEvent("Swim", "Swim for 2K free style", rose, SelectionType.SINGLE) }

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform GET request and verify response
        client
            .get()
            .uri("/events/${event0.id}")
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Event::class.java)
            .isEqualTo(event0)
    }

    @Test
    fun `getEventById should return 404 if event not found`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform GET request and verify response
        client
            .get()
            .uri("/events/999")
            .exchange()
            .expectStatus()
            .isNotFound
    }

    @Test
    fun `createEvent should return 201 if event created successfully`() {
        val rose =
            trxManager.run {
                repoUsers.createUser(
                    "Rose Mary",
                    "rose@example.com",
                    PasswordValidationInfo(newTokenValidationData()),
                )
            }
        val event = EventInput("Fun", "Hang around and have fun.", rose.id, SelectionType.MULTIPLE)

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform POST request and verify response
        client
            .post()
            .uri("/events")
            .bodyValue(event)
            .exchange()
            .expectStatus()
            .isOk
            .expectBody(Event::class.java)
            .also {
                val eventId = trxManager.run { repoEvents.findAll().last().id }
                it.isEqualTo(Event(eventId, event.title, event.description, rose, event.selectionType))
            }
    }

    @Test
    fun `createEvent should return 404 if organizer not found`() {
        // Mock event service to return EventError.ParticipantNotFound
        val eventInput = EventInput("Event 777", "Description 1", 999, SelectionType.SINGLE)

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform POST request and verify response
        client
            .post()
            .uri("/events")
            .bodyValue(eventInput)
            .exchange()
            .expectStatus()
            .isNotFound
    }
}
