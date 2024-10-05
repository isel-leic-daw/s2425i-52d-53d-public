package pt.isel

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import pt.isel.mem.TransactionManagerInMem
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventServiceTest {

    private lateinit var serviceEvent: EventService
    private lateinit var serviceUser: UserService

    @BeforeEach
    fun setup() {
        val trxManager = TransactionManagerInMem()
        serviceEvent = EventService(trxManager)
        serviceUser = UserService(trxManager)
    }

    @Test
    fun `addParticipantToTimeSlot should add participant to a time slot`() {
        val timeSlotId = 0
        val organizer = serviceUser.createUser("John", "john@example.com")
        assertIs<Success<User>>(organizer)

        serviceEvent
            .createEvent("Meeting", null, organizer.value.id, SelectionType.MULTIPLE)
            .also {
                val ev = it as Success<Event>
                serviceEvent.createFreeTimeSlot(ev.value.id, LocalDateTime.now(), 60)
            }


        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(timeSlotId, organizer.value.id)

        assertTrue(updatedTimeSlot is Success)
        assertIs<TimeSlotMultiple>(updatedTimeSlot.value)

        val participants = serviceEvent.getParticipantsInTimeSlot(timeSlotId)
        assertIs<Success<List<Participant>>>(participants)
        assertEquals(1, participants.value.size)
        assertEquals(organizer.value, participants.value[0].user)
    }

    @Test
    fun `addParticipantToTimeSlot should return Error already allocated for a Single Time slot with an owner`() {
        val timeSlotId = 0
        val organizer = serviceUser
            .createUser("John", "john@example.com")
            .let { check(it is Success); it.value }

        serviceEvent
            .createEvent("Meeting", null, organizer.id, SelectionType.SINGLE)
            .also {
                check(it is Success)
                serviceEvent.createFreeTimeSlot(it.value.id, LocalDateTime.now(), 60)
            }

        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(timeSlotId, organizer.id)

        assertIs<Success<TimeSlot>>(updatedTimeSlot)
        assertIs<TimeSlotSingle>(updatedTimeSlot.value)
        val owner = (updatedTimeSlot.value as TimeSlotSingle).owner
        assertNotNull(owner)
        owner.run {
            assertEquals(id, organizer.id)
            assertEquals(name, organizer.name)
            assertEquals(email, organizer.email)
        }
        val otherUser = serviceUser.createUser("john", "john@rambo.com")
        assertIs<Success<User>>(otherUser)
        val res = serviceEvent.addParticipantToTimeSlot(timeSlotId, otherUser.value.id)
        assertIs<Failure<EventError>>(res)
        assertIs<EventError.SingleTimeSlotAlreadyAllocated>(res.value)
    }

    @Test
    fun `addParticipantToTimeSlot should return UserNotFound when participant is not found`() {
        val timeSlotId = 0
        serviceUser
            .createUser("Organizer", "organizer@example.com")
            .let { check(it is Success); it.value }
            .let { participant -> serviceEvent.createEvent("Meeting", null, participant.id, SelectionType.MULTIPLE) }
            .let { check(it is Success); it.value }
            .also { event -> serviceEvent.createFreeTimeSlot(event.id, LocalDateTime.now(), 60) }

        // Try to add unknown participant
        val result = serviceEvent.addParticipantToTimeSlot(timeSlotId, 9999)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.UserNotFound)
    }

    @Test
    fun `createUser should create and return a participant`() {
        val name = "Alice"
        val email = "alice@example.com"

        val result = serviceUser.createUser(name, email)

        assertIs<Success<User>>(result)
        assertEquals(name, result.value.name)
        assertEquals(email, result.value.email)

    }

    @Test
    fun `createUser with already used email should return an error`() {
        serviceUser.createUser("Alice", "alice@example.com",)

        val result: Either<UserError.AlreadyUsedEmailAddress, User> =
            serviceUser.createUser("Mary", "alice@example.com")

        assertIs<Failure<UserError>>(result)
        assertIs<UserError>(result.value)
    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type SINGLE`() {
        val eventId = 0
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        serviceUser
            .createUser("Organizer", "organizer@example.com")
            .let { check(it is Success); it.value }
            .also { participant -> serviceEvent.createEvent("Meeting", null, participant.id, SelectionType.SINGLE) }


        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)
        val event = serviceEvent.getEventById(0).let { check(it is Success); it.value }
        val expected = TimeSlotSingle(0, startTime, durationInMinutes, event, null)
        assertTrue(result is Success)
        assertEquals(expected, result.value)

    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type MULTIPLE`() {
        val eventId = 0
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        serviceUser
            .createUser("Organizer", "organizer@example.com")
            .let { check(it is Success); it.value }
            .also { participant -> serviceEvent.createEvent("Meeting", null, participant.id, SelectionType.MULTIPLE) }

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)
        val event = serviceEvent.getEventById(0).let { check(it is Success); it.value }
        val expected = TimeSlotMultiple(0, startTime, durationInMinutes, event)
        assertTrue(result is Success)
        assertEquals(expected, result.value)
    }

    @Test
    fun `createFreeTimeSlot should return EventNotFound when event is not found`() {
        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.EventNotFound)
    }
}
