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
    private lateinit var serviceParticipant: ParticipantService

    @BeforeEach
    fun setup() {
        val trxManager = TransactionManagerInMem()
        serviceEvent = EventService(trxManager)
        serviceParticipant = ParticipantService(trxManager)
    }

    @Test
    fun `addParticipantToTimeSlot should add participant to a time slot`() {
        val timeSlotId = 0
        val participant = serviceParticipant.createParticipant("John", "john@example.com", ParticipantKind.GUEST)
        check(participant is Success)

        serviceEvent
            .createEvent("Meeting", null, participant.value.id, SelectionType.MULTIPLE)
            .also {
                val ev = it as Success<Event>
                serviceEvent.createFreeTimeSlot(ev.value.id, LocalDateTime.now(), 60)
            }


        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(timeSlotId, participant.value.id)

        assertTrue(updatedTimeSlot is Success)
        assertEquals((updatedTimeSlot.value as TimeSlotMultiple).participants.size, 1)
        assertEquals((updatedTimeSlot.value as TimeSlotMultiple).participants[0], participant.value)
    }

    @Test
    fun `addParticipantToTimeSlot should return Error already allocated for a Single Time slot with an owner`() {
        val timeSlotId = 0
        val participant = serviceParticipant
            .createParticipant("John", "john@example.com", ParticipantKind.GUEST)
            .let { check(it is Success); it.value }

        serviceEvent
            .createEvent("Meeting", null, participant.id, SelectionType.SINGLE)
            .also {
                check(it is Success)
                serviceEvent.createFreeTimeSlot(it.value.id, LocalDateTime.now(), 60)
            }

        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(timeSlotId, participant.id)

        assertIs<Success<TimeSlot>>(updatedTimeSlot)
        assertIs<TimeSlotSingle>(updatedTimeSlot.value)
        val owner = (updatedTimeSlot.value as TimeSlotSingle).owner
        assertNotNull(owner)
        owner.run {
            assertEquals(id, participant.id)
            assertEquals(name, participant.name)
            assertEquals(email, participant.email)
        }
        val res = serviceEvent.addParticipantToTimeSlot(timeSlotId, 99)
        assertIs<Failure<EventError>>(res)
        assertIs<EventError.SingleTimeSlotAlreadyAllocated>(res.value)
    }

    @Test
    fun `addParticipantToTimeSlot should return ParticipantNotFound when participant is not found`() {
        val timeSlotId = 0
        serviceParticipant
            .createParticipant("Organizer", "organizer@example.com", ParticipantKind.ORGANIZER)
            .let { check(it is Success); it.value }
            .let { participant -> serviceEvent.createEvent("Meeting", null, participant.id, SelectionType.MULTIPLE) }
            .let { check(it is Success); it.value }
            .also { event -> serviceEvent.createFreeTimeSlot(event.id, LocalDateTime.now(), 60) }

        // Try to add unknown participant
        val result = serviceEvent.addParticipantToTimeSlot(timeSlotId, 9999)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.ParticipantNotFound)
    }

    @Test
    fun `createParticipant should create and return a participant`() {
        val name = "Alice"
        val email = "alice@example.com"
        val kind = ParticipantKind.GUEST

        val result = serviceParticipant.createParticipant(name, email, kind)

        assertIs<Success<Participant>>(result)
        assertEquals(name, result.value.name)
        assertEquals(email, result.value.email)
        assertEquals(kind, result.value.kind)
    }

    @Test
    fun `createParticipant with already used email should return an error`() {
        serviceParticipant.createParticipant(
            "Alice",
            "alice@example.com",
            ParticipantKind.GUEST)

        val result: Either<ParticipantError.AlreadyUsedEmailAddress, Participant> =
            serviceParticipant.createParticipant("Mary", "alice@example.com", ParticipantKind.ORGANIZER)

        assertIs<Failure<ParticipantError>>(result)
        assertIs<ParticipantError>(result.value)
    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type SINGLE`() {
        val eventId = 0
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        serviceParticipant
            .createParticipant("Organizer", "organizer@example.com", ParticipantKind.ORGANIZER)
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
        serviceParticipant
            .createParticipant("Organizer", "organizer@example.com", ParticipantKind.ORGANIZER)
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
