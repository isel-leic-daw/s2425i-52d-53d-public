package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.web.reactive.server.WebTestClient
import pt.isel.model.TimeSlotInput
import java.time.LocalDateTime
import kotlin.test.Test

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
abstract class AbstractTimeSlotControllerTest {

    @Autowired
    lateinit var eventService: EventService

    @Autowired
    lateinit var userService: UserService

    @Autowired
    private lateinit var trxManager: TransactionManager

    @Autowired
    private lateinit var webTestClient: WebTestClient

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
    fun `create free time slot`() {
        // Arrange
        val organizer = userService.createUser("John", "john@example.com")
        check(organizer is Success)
        val event = eventService.createEvent("Meeting", "Team meeting", organizer.value.id, SelectionType.SINGLE)
        check(event is Success)
        val timeSlotInput = TimeSlotInput(LocalDateTime.now(), 60)

        // Act & Assert
        webTestClient.post()
            .uri("/api/events/${event.value.id}/timeslots")
            .bodyValue(timeSlotInput)
            .exchange()
            .expectStatus().isCreated
            .expectBody()
            .jsonPath("id").isNotEmpty
            .jsonPath("startTime").isEqualTo(timeSlotInput.startTime.toString())
            .jsonPath("durationInMinutes").isEqualTo(timeSlotInput.durationInMinutes)
    }

    @Test
    fun `add participant to time slot`() {
        // Arrange
        val organizer = userService.createUser("John", "john@example.com")
        check(organizer is Success)
        val guest = userService.createUser("Jane", "jane@example.com")
        check(guest is Success)
        val event = eventService.createEvent("Workshop", "Tech workshop", organizer.value.id, SelectionType.SINGLE)
        check(event is Success)
        val timeSlot = eventService.createFreeTimeSlot(event.value.id, LocalDateTime.now(), 120)
        check(timeSlot is Success)

        // Act & Assert
        webTestClient.post()
            .uri("/api/events/${event.value.id}/timeslots/${timeSlot.value.id}/participants/${guest.value.id}")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("owner.id").isEqualTo(guest.value.id)
            .jsonPath("owner.name").isEqualTo(guest.value.name)
    }

    @Test
    fun `fail to add participant to already allocated single time slot`() {
        // Arrange
        val organizer = userService.createUser("John", "john@example.com")
        check(organizer is Success)
        val guest1 = userService.createUser("Jane", "jane@example.com")
        check(guest1 is Success)
        val guest2 = userService.createUser("Paul", "paul@example.com")
        check(guest2 is Success)
        val event = eventService.createEvent("Training", "Employee training", organizer.value.id, SelectionType.SINGLE)
        check(event is Success)
        // Create a timeslot on Event and allocate it to guest 1
        var timeSlot = eventService
            .createFreeTimeSlot(event.value.id, LocalDateTime.now(), 90)
            .let { it as Success }
            .let { eventService.addParticipantToTimeSlot(it.value.id, guest1.value.id) }
        check(timeSlot is Success)

        // Act & Assert
        // Try to allocate slot to guest 2
        webTestClient.post()
            .uri("/api/events/${event.value.id}/timeslots/${timeSlot.value.id}/participants/${guest2.value.id}")
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectBody()
            .jsonPath("title").isEqualTo("timeslot-already-allocated")
    }
}
