package pt.isel

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.postgresql.ds.PGSimpleDataSource
import java.time.LocalDateTime
import kotlin.test.assertEquals

class RepositoryJdbiTests {
    companion object {
        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        setURL(Environment.getDbUrl())
                    },
                ).configureWithAppRequirements()
    }

    @BeforeEach
    fun clean() {
        runWithHandle { handle: Handle ->
            RepositoryParticipantJdbi(handle).clear()
            RepositoryTimeSlotJdbi(handle).clear()
            RepositoryEventJdbi(handle).clear()
            RepositoryUserJdbi(handle).clear()
        }
    }

    @Test
    fun `test create event and find it`() =
        runWithHandle { handle ->
            val alice =
                RepositoryUserJdbi(handle).createUser(
                    "Alice",
                    "alice@example.com",
                )
            val repoEvents =
                RepositoryEventJdbi(handle).also {
                    it.createEvent(
                        title = "Team Meeting",
                        description = "Discuss project updates",
                        organizer = alice,
                        selectionType = SelectionType.SINGLE,
                    )
                }
            val events = repoEvents.findAll()
            assertEquals(1, events.size)
        }

    @Test
    fun `test replacing a time slot in an event`() =
        runWithHandle { handle ->
            val repoUsers =
                RepositoryUserJdbi(handle).also {
                    it.createUser(
                        "Alice",
                        "alice@example.com",
                    )
                }
            val repoTimeSlots = RepositoryTimeSlotJdbi(handle)
            assertEquals(0, repoTimeSlots.findAll().size)

            val repoEvents =
                RepositoryEventJdbi(handle).also {
                    it.createEvent(
                        title = "Team Meeting",
                        description = "Discuss project updates",
                        organizer = repoUsers.findAll()[0],
                        selectionType = SelectionType.SINGLE,
                    )
                }

            val event = repoEvents.findAll().first()

            val slot1 =
                repoTimeSlots.createTimeSlotSingle(
                    startTime = LocalDateTime.of(2024, 9, 30, 10, 0),
                    durationInMinutes = 60,
                    event,
                )
            val slot2 =
                repoTimeSlots.createTimeSlotSingle(
                    startTime = LocalDateTime.of(2024, 9, 30, 11, 0),
                    durationInMinutes = 60,
                    event,
                )
            val events = repoTimeSlots.findAllByEvent(event)
            assertEquals(2, events.size)

            assertEquals(setOf(slot1, slot2), events.toSet())

            val newSlot = TimeSlotSingle(slot2.id, LocalDateTime.of(2024, 9, 30, 10, 30), 60, event)
            repoTimeSlots.save(newSlot)

            val slots = repoTimeSlots.findAllByEvent(event).toSet()

            assertEquals(2, slots.size)
            assertEquals(setOf(slot1, newSlot), repoTimeSlots.findAllByEvent(event).toSet())
        }
}
