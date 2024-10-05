package pt.isel

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.LocalDateTime

class AgendifyTest {

    private val organizer = User(1, "Alice", "alice@example.com")
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

        val newSlot = TimeSlotMultiple(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            eventMultiple)

        assertEquals(eventMultiple, newSlot.event)
    }

    @Test
    fun `test adding a participant to a TimeSlotSingle`() {
        val owner = User(2, "Bob", "bob@example.com")

        val slot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            eventSingle,
            owner
        )

        assertEquals(owner.name, "Bob")
    }

    @Test
    fun `test removing a participant from a TimeSlotSingle`() {
        val participant = User(2, "Bob", "bob@example.com")
        val slot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            owner = participant,
            event = eventSingle
        )

        val updatedSlot = slot.removeOwner(participant)

        assertNull(updatedSlot.owner)
    }


    @Test
    fun `test trying to add a participant when TimeSlotSingle already has an owner`() {
        val participant1 = User(2, "Bob", "bob@example.com")
        val participant2 = User(3, "Charlie", "charlie@example.com")
        val slot = TimeSlotSingle(
            id = 1,
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            owner = participant1,
            event = eventSingle
        )

        val exception = assertThrows<IllegalStateException> {
            slot.addOwner(participant2)
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
        val participant = Participant(2, User(2, "Bob", "bob@example.com"), slot)

        assertEquals(participant.slot.id, slot.id)
    }

}
