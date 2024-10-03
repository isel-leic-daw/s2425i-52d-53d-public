package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.mem.RepositoryEventInMem
import pt.isel.mem.RepositoryParticipantInMem
import pt.isel.mem.RepositoryTimeslotInMem
import pt.isel.mem.TransactionManagerInMem
import pt.isel.model.EventInput


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class EventControllerTest {

    // Injected by the test environment
    @LocalServerPort
    var port: Int = 0

    @Autowired
    private lateinit var trxManager: TransactionManager

    @BeforeEach
    fun setUp() {
        trxManager.run {
            repoEvents.clear()
            repoParticipants.clear()
            repoSlots.clear()
        }
    }

    @Test
    fun `getAllEvents should return a list of events`() {
        val rose = trxManager.run { repoParticipants.createParticipant("Rose Mary", "rose@example.com", ParticipantKind.ORGANIZER) }
        val event0 = trxManager.run { repoEvents.createEvent("Swim", "Swim for 2K free style", rose, SelectionType.SINGLE) }
        val event1 = trxManager.run { repoEvents.createEvent("Status Meeting", "Coffee break and more", rose, SelectionType.MULTIPLE) }

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform GET request and verify response
        client.get()
            .uri("/events")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Event::class.java)
            .hasSize(2)
            .contains(event1, event0)
    }

    @Test
    fun `getEventById should return an event if found`() {
        val rose = trxManager.run { repoParticipants.createParticipant("Rose Mary", "rose@example.com", ParticipantKind.ORGANIZER) }
        val event0 = trxManager.run { repoEvents.createEvent("Swim", "Swim for 2K free style", rose, SelectionType.SINGLE) }

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform GET request and verify response
        client.get()
            .uri("/events/0")
            .exchange()
            .expectStatus().isOk
            .expectBody(Event::class.java)
            .isEqualTo(event0)
    }

    @Test
    fun `getEventById should return 404 if event not found`() {
        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform GET request and verify response
        client.get()
            .uri("/events/999")
            .exchange()
            .expectStatus().isNotFound
    }

    @Test
    fun `createEvent should return 201 if event created successfully`() {
        val rose = trxManager.run { repoParticipants.createParticipant("Rose Mary", "rose@example.com", ParticipantKind.ORGANIZER) }
        val event = EventInput("Fun", "Hang around and have fun.", rose.id, SelectionType.MULTIPLE)

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform POST request and verify response
        client.post()
            .uri("/events")
            .bodyValue(event)
            .exchange()
            .expectStatus().isOk
            .expectBody(Event::class.java)
            .isEqualTo(Event(0, event.title, event.description, rose, event.selectionType))
    }

    @Test
    fun `createEvent should return 404 if organizer not found`() {
        // Mock event service to return EventError.ParticipantNotFound
        val eventInput = EventInput("Event 777", "Description 1", 999, SelectionType.SINGLE)

        // given: an HTTP client
        val client = WebTestClient.bindToServer().baseUrl("http://localhost:$port/api").build()

        // Perform POST request and verify response
        client.post()
            .uri("/events")
            .bodyValue(eventInput)
            .exchange()
            .expectStatus().isNotFound
    }
}
