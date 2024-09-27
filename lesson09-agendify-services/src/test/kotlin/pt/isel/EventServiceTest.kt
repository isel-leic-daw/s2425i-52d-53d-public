package pt.isel

import io.mockk.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EventServiceTest {

    private lateinit var eventRepository: RepositoryEvent
    private lateinit var participantRepository: RepositoryParticipant
    private lateinit var timeSlotRepository: RepositoryTimeSlot
    private lateinit var eventService: EventService

    @BeforeEach
    fun setup() {
        eventRepository = mockk()
        participantRepository = mockk()
        timeSlotRepository = mockk()
        eventService = EventService(eventRepository, participantRepository, timeSlotRepository)
    }

    @Test
    fun `addParticipantToTimeSlot should add participant to a time slot`() {
        val eventId = 1
        val timeSlotId = 2
        val participantId = 3
        val participant = Participant(participantId, "John", "john@example.com", ParticipantKind.GUEST)
        val timeSlot = TimeSlotMultiple(timeSlotId, LocalDateTime.now(), 60)
        val event = Event(eventId, "Meeting", null, participant, SelectionType.MULTIPLE, listOf(timeSlot))

        every { eventRepository.findById(eventId) } returns event
        every { participantRepository.findById(participantId) } returns participant
        every { timeSlotRepository.save(any()) } just Runs


        val result = eventService.addParticipantToTimeSlot(eventId, timeSlotId, participantId)

        assertTrue(result is Success)
        assertEquals((result.value.timeSlots[0] as TimeSlotMultiple).participants.size, 1)
        assertEquals((result.value.timeSlots[0] as TimeSlotMultiple).participants[0], participant)

        verify { timeSlotRepository.save(any()) }
    }

    @Test
    fun `addParticipantToTimeSlot should return EventNotFound when event is not found`() {
        val eventId = 1
        val timeSlotId = 2
        val participantId = 3

        every { eventRepository.findById(eventId) } returns null

        val result = eventService.addParticipantToTimeSlot(eventId, timeSlotId, participantId)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.EventNotFound)
    }

    @Test
    fun `addParticipantToTimeSlot should return ParticipantNotFound when participant is not found`() {
        val eventId = 1
        val timeSlotId = 2
        val participantId = 3
        val timeSlot = TimeSlotMultiple(timeSlotId, LocalDateTime.now(), 60)
        val event = Event(eventId, "Meeting", null, Participant(1, "Organizer", "organizer@example.com", ParticipantKind.ORGANIZER), SelectionType.MULTIPLE, listOf(timeSlot))

        every { eventRepository.findById(eventId) } returns event
        every { participantRepository.findById(participantId) } returns null

        val result = eventService.addParticipantToTimeSlot(eventId, timeSlotId, participantId)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.ParticipantNotFound)
    }

    @Test
    fun `createParticipant should create and return a participant`() {
        val name = "Alice"
        val email = "alice@example.com"
        val kind = ParticipantKind.GUEST
        val participant = Participant(1, name, email, kind)

        every { participantRepository.createParticipant(name, email, kind) } returns participant

        val result = eventService.createParticipant(name, email, kind)

        assertTrue(result is Success)
        assertEquals(result.value, participant)
    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type SINGLE`() {
        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val event = Event(eventId, "Meeting", null, Participant(1, "Organizer", "organizer@example.com", ParticipantKind.ORGANIZER), SelectionType.SINGLE, emptyList())
        val timeSlotSingle = TimeSlotSingle(1, startTime, durationInMinutes)

        every { eventRepository.findById(eventId) } returns event
        every { timeSlotRepository.createTimeSlotSingle(eventId, startTime, durationInMinutes) } returns timeSlotSingle

        val result = eventService.createFreeTimeSlot(eventId, startTime, durationInMinutes)

        assertTrue(result is Success)
        assertEquals(result.value, timeSlotSingle)
    }

    @Test
    fun `createFreeTimeSlot should create a free time slot based on event selection type MULTIPLE`() {
        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val event = Event(eventId, "Meeting", null, Participant(1, "Organizer", "organizer@example.com", ParticipantKind.ORGANIZER), SelectionType.MULTIPLE, emptyList())
        val timeSlotMultiple = TimeSlotMultiple(1, startTime, durationInMinutes)

        every { eventRepository.findById(eventId) } returns event
        every { timeSlotRepository.createTimeSlotMultiple(eventId, startTime, durationInMinutes) } returns timeSlotMultiple

        val result = eventService.createFreeTimeSlot(eventId, startTime, durationInMinutes)

        assertTrue(result is Success)
        assertEquals(result.value, timeSlotMultiple)
    }

    @Test
    fun `createFreeTimeSlot should return EventNotFound when event is not found`() {
        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60

        every { eventRepository.findById(eventId) } returns null

        val result = eventService.createFreeTimeSlot(eventId, startTime, durationInMinutes)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.EventNotFound)
    }
}
