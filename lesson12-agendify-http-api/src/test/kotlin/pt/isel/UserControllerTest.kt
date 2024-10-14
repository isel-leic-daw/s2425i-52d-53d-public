package pt.isel

import org.jdbi.v3.core.Jdbi
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.MethodSource
import org.postgresql.ds.PGSimpleDataSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import pt.isel.mem.TransactionManagerInMem
import pt.isel.model.UserCreateTokenInputModel
import pt.isel.model.UserCreateTokenOutputModel
import pt.isel.model.UserHomeOutputModel
import pt.isel.model.UserInput
import java.util.stream.Stream
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNotNull
import kotlin.test.assertTrue
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.minutes

class UserControllerTest {
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
    fun `can create an user, obtain a token, and access user home, and logout`(trxManager: TransactionManager) {
        val controllerUser = UserController(createUserService(trxManager, TestClock()))

        // given: a user
        val name = "John Rambo"
        val email = "john@rambo.vcom"
        val password = "badGuy"

        // when: creating an user
        // then: the response is a 201 with a proper Location header
        val userId =
            controllerUser.createUser(UserInput(name, email, password)).let { resp ->
                assertEquals(HttpStatus.CREATED, resp.statusCode)
                val location = resp.headers.getFirst(HttpHeaders.LOCATION)
                assertNotNull(location)
                assertTrue(location.startsWith("/api/users"))
                location.split("/").last().toInt()
            }

        // when: creating a token
        // then: the response is a 200
        val token =
            controllerUser.token(UserCreateTokenInputModel(email, password)).let { resp ->
                assertEquals(HttpStatus.OK, resp.statusCode)
                assertIs<UserCreateTokenOutputModel>(resp.body)
                (resp.body as UserCreateTokenOutputModel).token
            }

        // when: getting the user home with a valid token
        // then: the response is a 200 with the proper representation
        val user = User(userId, name, email, PasswordValidationInfo(password))
        controllerUser.userHome(AuthenticatedUser(user, token)).also { resp ->
            assertEquals(HttpStatus.OK, resp.statusCode)
            assertEquals(UserHomeOutputModel(userId, name, email), resp.body)
        }
    }
}
