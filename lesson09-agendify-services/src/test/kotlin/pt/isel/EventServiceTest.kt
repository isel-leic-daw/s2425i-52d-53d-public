package pt.isel

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.mem.TransactionManagerInMem
import java.time.LocalDateTime
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class EventServiceTest {
    companion object {
        private val jdbi =
            Jdbi
                .create(
                    PGSimpleDataSource().apply {
                        setURL(Environment.getDbUrl())
                    },
                ).configureWithAppRequirements()

        @JvmStatic
        fun transactionManagers(): Stream<TransactionManager> =
            Stream.of(
                TransactionManagerInMem().also { cleanup(it) },
                TransactionManagerJdbi(jdbi).also { cleanup(it) },
            )

        private fun cleanup(trxManager: TransactionManager) {
            trxManager.run {
                repoParticipants.clear()
                repoSlots.clear()
                repoEvents.clear()
                repoUsers.clear()
            }
        }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `addParticipantToTimeSlot should add participant to a time slot`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager, UsersDomain(BCryptPasswordEncoder()))

        val organizer = serviceUser.createUser("John", "john@example.com", "camafeuAtleta")
        assertIs<Success<User>>(organizer)

        val ev =
            serviceEvent
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
    fun `addParticipantToTimeSlot should return Error already allocated for a Single Time slot with an owner`(
        trxManager: TransactionManager,
    ) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager, UsersDomain(BCryptPasswordEncoder()))

        val organizer =
            serviceUser
                .createUser("John", "john@example.com", "janitaSalome")
                .let {
                    check(it is Success)
                    it.value
                }

        val ts =
            serviceEvent
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
        val otherUser = serviceUser.createUser("john", "john@rambo.com", "camafeuAtleta")
        assertIs<Success<User>>(otherUser)
        val res = serviceEvent.addParticipantToTimeSlot(ts.value.id, otherUser.value.id)
        assertIs<Failure<EventError>>(res)
        assertIs<EventError.SingleTimeSlotAlreadyAllocated>(res.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `addParticipantToTimeSlot should return UserNotFound when participant is not found`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager, UsersDomain(BCryptPasswordEncoder()))

        val ts =
            serviceUser
                .createUser("Organizer", "organizer@example.com", "camafeuAtleta")
                .let {
                    check(it is Success)
                    it.value
                }.let { participant ->
                    serviceEvent.createEvent(
                        "Meeting",
                        null,
                        participant.id,
                        SelectionType.MULTIPLE,
                    )
                }.let {
                    check(it is Success)
                    it.value
                }.let { event -> serviceEvent.createFreeTimeSlot(event.id, LocalDateTime.now(), 60) }
        assertIs<Success<TimeSlot>>(ts)

        // Try to add unknown participant
        val result = serviceEvent.addParticipantToTimeSlot(ts.value.id, -9999)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.UserNotFound)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createUser should create and return a participant`(trxManager: TransactionManager) {
        val usersDomain = UsersDomain(BCryptPasswordEncoder())
        val serviceUser = UserService(trxManager, usersDomain)

        val name = "Alice"
        val email = "alice@example.com"
        val pass = "camafeuAtleta"

        val result = serviceUser.createUser(name, email, pass)

        assertIs<Success<User>>(result)
        assertEquals(name, result.value.name)
        assertEquals(email, result.value.email)
        assertTrue { usersDomain.validatePassword(pass, result.value.passwordValidation) }
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createUser with already used email should return an error`(trxManager: TransactionManager) {
        val serviceUser = UserService(trxManager, UsersDomain(BCryptPasswordEncoder()))

        serviceUser.createUser("Alice", "alice@example.com", "camafeuAtleta")

        val result: Either<UserError, User> =
            serviceUser.createUser("Mary", "alice@example.com", "janitaSalome")

        assertIs<Failure<UserError>>(result)
        assertIs<UserError>(result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createFreeTimeSlot should create a free time slot based on event selection type SINGLE`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager, UsersDomain(BCryptPasswordEncoder()))

        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val organizer =
            serviceUser
                .createUser("Organizer", "organizer@example.com", "camafeuAtleta")
                .let {
                    check(it is Success)
                    it.value
                }
        val eventId =
            serviceEvent
                .createEvent("Meeting", null, organizer.id, SelectionType.SINGLE)
                .let {
                    check(it is Success)
                    it.value.id
                }

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)
        assertTrue(result is Success)

        val event =
            serviceEvent.getEventById(eventId).let {
                check(it is Success)
                it.value
            }
        val expected = TimeSlotSingle(result.value.id, startTime, durationInMinutes, event, null)
        assertEquals(expected, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createFreeTimeSlot should create a free time slot based on event selection type MULTIPLE`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)
        val serviceUser = UserService(trxManager, UsersDomain(BCryptPasswordEncoder()))

        val startTime = LocalDateTime.now()
        val durationInMinutes = 60
        val organizer =
            serviceUser
                .createUser("Organizer", "organizer@example.com", "camafeuAtleta")
                .let {
                    check(it is Success)
                    it.value
                }
        val eventId =
            serviceEvent
                .createEvent("Meeting", null, organizer.id, SelectionType.MULTIPLE)
                .let {
                    check(it is Success)
                    it.value.id
                }

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)
        assertTrue(result is Success)

        val event =
            serviceEvent.getEventById(eventId).let {
                check(it is Success)
                it.value
            }
        val expected = TimeSlotMultiple(result.value.id, startTime, durationInMinutes, event)
        assertEquals(expected, result.value)
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `createFreeTimeSlot should return EventNotFound when event is not found`(trxManager: TransactionManager) {
        val serviceEvent = EventService(trxManager)

        val eventId = 1
        val startTime = LocalDateTime.now()
        val durationInMinutes = 60

        val result = serviceEvent.createFreeTimeSlot(eventId, startTime, durationInMinutes)

        assertTrue(result is Failure)
        assertEquals(result.value, EventError.EventNotFound)
    }
}
