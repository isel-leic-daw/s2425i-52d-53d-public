package pt.isel

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class AgendifyTest {

    private val organizer = Participant(id = 1, name = "Alice", email = "alice@example.com", kind = ParticipantKind.ORGANIZER)
    private val eventSingle = Event(
        id = 1,
        title = "Team Meeting",
        description = "Discuss project updates",
        organizer = organizer,
        selectionType = SelectionType.SINGLE
    )
    private val eventMultiple = Event(
        id = 1,
        title = "Team Building",
        description = "Dinner and chatting",
        organizer = organizer,
        selectionType = SelectionType.MULTIPLE
    )


    @Test
    fun `test adding a time slot to an event`() {

        val newSlot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            eventSingle)

        assertEquals(eventSingle, newSlot.event)
    }

    @Test
    fun `test adding a participant to a TimeSlotSingle`() {
        val slot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            eventSingle
        )
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)

        val updatedSlot = slot.addParticipant(participant)

        assertEquals(participant, updatedSlot.owner)
    }

    @Test
    fun `test removing a participant from a TimeSlotSingle`() {
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)
        val slot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            owner = participant,
            event = eventSingle
        )

        val updatedSlot = slot.removeParticipant(participant)

        assertNull(updatedSlot.owner)
    }

    @Test
    fun `test trying to add a participant when TimeSlotSingle already has an owner`() {
        val participant1 = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)
        val participant2 =
            Participant(id = 3, name = "Charlie", email = "charlie@example.com", kind = ParticipantKind.GUEST)
        val slot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            owner = participant1,
            event = eventSingle
        )

        val exception = assertThrows<IllegalStateException> {
            slot.addParticipant(participant2)
        }
        assertEquals("This time slot is already allocated to a participant.", exception.message)
    }

    @Test
    fun `test adding a participant to TimeSlotMultiple`() {
        val slot = TimeSlotMultiple(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            eventMultiple
        )
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)

        val updatedSlot = slot.addParticipant(participant)

        assertTrue(updatedSlot.participants.contains(participant))
    }

    @Test
    fun `test removing a participant from TimeSlotMultiple`() {
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)
        val slot = TimeSlotMultiple(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            participants = listOf(participant),
            event = eventMultiple
        )

        val updatedSlot = slot.removeParticipant(participant)

        assertFalse(updatedSlot.participants.contains(participant))
    }
}
