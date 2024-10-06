package pt.isel

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import pt.isel.mem.TransactionManagerInMem
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventServiceTest {
    companion object {
        private fun runWithHandle(block: (Handle) -> Unit) = jdbi.useTransaction<Exception>(block)

        private val jdbi =
            Jdbi.create(
                PGSimpleDataSource().apply {
                    setURL("jdbc:postgresql://localhost:5432/db?user=dbuser&password=changeit")
                },
            ).configureWithAppRequirements()

        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> {
            return Stream.of(
                TransactionManagerInMem(),
                TransactionManagerJdbi(jdbi)
            )
        }
    }


    @BeforeEach
    fun setup() {
        runWithHandle { handle: Handle ->
            RepositoryParticipantJdbi(handle).clear()
            RepositoryTimeSlotJdbi(handle).clear()
            RepositoryEventJdbi(handle).clear()
            RepositoryUserJdbi(handle).clear()
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `addParticipantToTimeSlot should add participant to a time slot`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val organizer = serviceUser.createUser("John", "john@example.com")
        assertIs<Success<User>>(organizer)

        val ev = serviceEvent
            .createEvent("Meeting", null, organizer.value.id, SelectionType.MULTIPLE)
            .let { it as Success<Event> }
        val ts = serviceEvent.createFreeTimeSlot(ev.value.id, LocalDateTime.now(), 60)
        assertIs<Success<TimeSlot>>(ts)
        val timeSlotId = ts.value.id

        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(timeSlotId, organizer.value.id)

        assertTrue(updatedTimeSlot is Success)
        assertIs<TimeSlotMultiple>(updatedTimeSlot.value)

        val participants = serviceEvent.getParticipantsInTimeSlot(timeSlotId)
        assertIs<Success<List<Participant>>>(participants)
        assertEquals(1, participants.value.size)
        assertEquals(organizer.value, participants.value[0].user)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `addParticipantToTimeSlot should return Error already allocated for a Single Time slot with an owner`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val organizer = serviceUser
            .createUser("John", "john@example.com")
            .let { check(it is Success); it.value }

        val ts = serviceEvent
            .createEvent("Meeting", null, organizer.id, SelectionType.SINGLE)
            .let {
                check(it is Success)
                serviceEvent.createFreeTimeSlot(it.value.id, LocalDateTime.now(), 60)
            }
        assertIs<Success<TimeSlot>>(ts)

        val updatedTimeSlot = serviceEvent.addParticipantToTimeSlot(ts.value.id, organizer.id)

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
        val res = serviceEvent.addParticipantToTimeSlot(ts.value.id, otherUser.value.id)
        assertIs<Failure<EventError>>(res)
        assertIs<EventError.SingleTimeSlotAlreadyAllocated>(res.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `addParticipantToTimeSlot should return UserNotFound when participant is not found`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val ts = serviceUser
            .createUser("Organizer", "organizer@example.com")
            .let { check(it is Success); it.value }
            .let { participant -> serviceEvent.createEvent("Meeting", null, participant.id, SelectionType.MULTIPLE) }
            .let { check(it is Success); it.value }
            .let { event -> serviceEvent.createFreeTimeSlot(event.id, LocalDateTime.now(), 60) }
        assertIs<Success<TimeSlot>>(ts)

        // Try to add unknown participant
        val result = serviceEvent.addParticipantToTimeSlot(ts.value.id, -9999)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.UserNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createUser should create and return a participant`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val name = "Alice"
        val email = "alice@example.com"

        val result = serviceUser.createUser(name, email)

        assertIs<Success<User>>(result)
        assertEquals(name, result.value.name)
        assertEquals(email, result.value.email)

    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createUser with already used email should return an error`(trxManager: TransactionManager) {
        val serviceUser = UserService(trxManager)

        serviceUser.createUser("Alice", "alice@example.com",)

        val result: Either<UserError.AlreadyUsedEmailAddress, User> =
            serviceUser.createUser("Mary", "alice@example.com")

        assertIs<Failure<UserError>>(result)
        assertIs<UserError>(result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createFreeTimeSlot should create a free time slot based on event selection type SINGLE`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val organizer = serviceUser
            .createUser("Organizer", "organizer@example.com")
            .let { check(it is Success); it.value }
        val eventId = serviceEvent
            .createEvent("Meeting", null, organizer.id, SelectionType.SINGLE)
            .let { check(it is Success); it.value.id }


        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)
        assertTrue(result is Success)

        val event = serviceEvent.getEventById(eventId).let { check(it is Success); it.value }
        val expected = TimeSlotSingle(result.value.id, startTime, durationInMinutes, event, null)
        assertEquals(expected, result.value)

    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createFreeTimeSlot should create a free time slot based on event selection type MULTIPLE`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val organizer = serviceUser
            .createUser("Organizer", "organizer@example.com")
            .let { check(it is Success); it.value }
        val eventId = serviceEvent
            .createEvent("Meeting", null, organizer.id, SelectionType.MULTIPLE)
            .let { check(it is Success); it.value.id }

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)
        assertTrue(result is Success)

        val event = serviceEvent.getEventById(eventId).let { check(it is Success); it.value }
        val expected = TimeSlotMultiple(result.value.id, startTime, durationInMinutes, event)
        assertEquals(expected, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createFreeTimeSlot should return EventNotFound when event is not found`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager)

        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.EventNotFound)
    }
}
