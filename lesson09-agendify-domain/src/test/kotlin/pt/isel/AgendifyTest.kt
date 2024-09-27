package pt.isel

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class AgendifyTest {

    private val organizer = Participant(id = 1, name = "Alice", email = "alice@example.com", kind = ParticipantKind.ORGANIZER)

    @Test
    fun `test adding a time slot to an event`() {
        val event = Event(
            id = 1,
            title = "Team Meeting",
            description = "Discuss project updates",
            organizer = organizer,
            selectionType = SelectionType.SINGLE,
            timeSlots = emptyList()
        )

        val newSlot = TimeSlotSingle(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60)

        val updatedEvent = event.addTimeSlot(newSlot)

        assertEquals(1, updatedEvent.timeSlots.size)
        assertEquals(newSlot, updatedEvent.timeSlots[0])
    }

    @Test
    fun `test replacing a time slot in an event`() {
        val slot1 = TimeSlotSingle(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60)
        val slot2 = TimeSlotSingle(id = 2, startTime = LocalDateTime.of(2024, 9, 30, 11, 0), durationInMinutes = 60)

        val event = Event(
            id = 1,
            title = "Team Meeting",
            description = "Discuss project updates",
            organizer = organizer,
            selectionType = SelectionType.SINGLE,
            timeSlots = listOf(slot1, slot2)
        )

        val newSlot = TimeSlotSingle(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 30), durationInMinutes = 60)

        val updatedEvent = event.replaceSlot(slotId = 1, newSlot = newSlot)

        assertEquals(2, updatedEvent.timeSlots.size)
        assertEquals(newSlot, updatedEvent.timeSlots[0]) // Slot 1 should be replaced
        assertEquals(slot2, updatedEvent.timeSlots[1]) // Slot 2 should remain unchanged
    }

    @Test
    fun `test adding a participant to a TimeSlotSingle`() {
        val slot = TimeSlotSingle(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60)
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)

        val updatedSlot = slot.addParticipant(participant)

        assertEquals(participant, updatedSlot.owner)
    }

    @Test
    fun `test removing a participant from a TimeSlotSingle`() {
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)
        val slot = TimeSlotSingle(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60, owner = participant)

        val updatedSlot = slot.removeParticipant(participant)

        assertNull(updatedSlot.owner)
    }

    @Test
    fun `test trying to add a participant when TimeSlotSingle already has an owner`() {
        val participant1 = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)
        val participant2 = Participant(id = 3, name = "Charlie", email = "charlie@example.com", kind = ParticipantKind.GUEST)
        val slot = TimeSlotSingle(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60, owner = participant1)

        val exception = assertThrows<IllegalStateException> {
            slot.addParticipant(participant2)
        }
        assertEquals("This time slot is already allocated to a participant.", exception.message)
    }

    @Test
    fun `test adding a participant to TimeSlotMultiple`() {
        val slot = TimeSlotMultiple(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60)
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)

        val updatedSlot = slot.addParticipant(participant)

        assertTrue(updatedSlot.participants.contains(participant))
    }

    @Test
    fun `test removing a participant from TimeSlotMultiple`() {
        val participant = Participant(id = 2, name = "Bob", email = "bob@example.com", kind = ParticipantKind.GUEST)
        val slot = TimeSlotMultiple(id = 1, startTime = LocalDateTime.of(2024, 9, 30, 10, 0), durationInMinutes = 60, participants = listOf(participant))

        val updatedSlot = slot.removeParticipant(participant)

        assertFalse(updatedSlot.participants.contains(participant))
    }
}
