package pt.isel

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.mem.TransactionManagerInMem
import java.util.stream.Stream
import kotlin.math.abs
import kotlin.random.Random
import kotlin.test.assertEquals
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

fun newTokenValidationData() = "token-${abs(Random.nextLong())}"

class EventControllerTest {
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

        private fun createUserService(
            trxManager: TransactionManager,
            testClock: TestClock,
            tokenTtl: Duration = 30.days,
            tokenRollingTtl: Duration = 30.minutes,
            maxTokensPerUser: Int = 3,
        ) = UserService(
            trxManager,
            UsersDomain(
                BCryptPasswordEncoder(),
                Sha256TokenEncoder(),
                UsersDomainConfig(
                    tokenSizeInBytes = 256 / 8,
                    tokenTtl = tokenTtl,
                    tokenRollingTtl,
                    maxTokensPerUser = maxTokensPerUser,
                ),
            ),
            testClock,
        )
    }

    @ParameterizedTest
    @MethodSource("transactionManagers")
    fun `getAllEvents should return a list of events`(trxManager: TransactionManager) {
        // Arrange
        val rose =
            trxManager.run {
                repoUsers.createUser(
                    "Rose Mary",
                    "rose@example.com",
                    PasswordValidationInfo(newTokenValidationData()),
                )
            }
        val event0 =
            trxManager.run { repoEvents.createEvent("Swim", "Swim for 2K free style", rose, SelectionType.SINGLE) }
        val event1 =
            trxManager.run {
                repoEvents.createEvent(
                    "Status Meeting",
                    "Coffee break and more",
                    rose,
                    SelectionType.MULTIPLE,
                )
            }
        val controllerEvents = EventController(EventService(trxManager))
        val resp = controllerEvents.getAllEvents()
        assertEquals(HttpStatus.OK, resp.statusCode)
        assertEquals(2, resp.body?.size)
    }
}
