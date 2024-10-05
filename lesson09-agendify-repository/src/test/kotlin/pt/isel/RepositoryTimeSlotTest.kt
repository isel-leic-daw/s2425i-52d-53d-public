package pt.isel

import pt.isel.mem.RepositoryEventInMem
import pt.isel.mem.RepositoryTimeslotInMem
import pt.isel.mem.RepositoryUserInMem
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class RepositoryTimeSlotTest {

    private val repoUsers = RepositoryUserInMem().also {
        it.createUser(
            "Alice", "alice@example.com"
        )
    }
    private val repoTimeSlots = RepositoryTimeslotInMem()

    @Test
    fun `test replacing a time slot in an event`() {
        val repoEvents = RepositoryEventInMem().also { it.createEvent(
            title = "Team Meeting",
            description = "Discuss project updates",
            organizer = repoUsers.findAll()[0],
            selectionType = SelectionType.SINGLE,
        ) }

        val event = repoEvents.findAll().first()

        val slot1 = repoTimeSlots.createTimeSlotSingle(
            startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
            durationInMinutes = 60,
            event
        )
        val slot2 = repoTimeSlots.createTimeSlotSingle(
            startTime = LocalDateTime.of(2024, 9, 30, 11, 0),
            durationInMinutes = 60,
            event
        )

        assertEquals(setOf(slot1, slot2), repoTimeSlots.findAllByEvent(event).toSet())

        val newSlot = TimeSlotSingle(slot2.id, LocalDateTime.of(2024, 9, 30, 10, 30), 60, event)
        repoTimeSlots.save(newSlot)

        val slots = repoTimeSlots.findAllByEvent(event).toSet()

        assertEquals(2, slots.size)
        assertEquals(setOf(slot1, newSlot), repoTimeSlots.findAllByEvent(event).toSet())
    }

}