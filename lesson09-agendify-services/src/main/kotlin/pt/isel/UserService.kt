package pt.isel

import jakarta.inject.Named
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant

data class TokenExternalInfo(
    val tokenValue: String,
    val tokenExpiration: Instant,
)

sealed class UserError {
    data object AlreadyUsedEmailAddress : UserError()

    data object InsecurePassword : UserError()
}

sealed class TokenCreationError {
    data object UserOrPasswordAreInvalid : TokenCreationError()
}

@Named
class UserService(
    private val trxManager: TransactionManager,
    val usersDomain: UsersDomain,
    private val clock: Clock,
) {
    /**
     * Create a new participant
     */
    fun createUser(
        name: String,
        email: String,
        password: String,
    ): Either<UserError, User> {
        if (!usersDomain.isSafePassword(password)) {
            return failure(UserError.InsecurePassword)
        }

        val passwordValidationInfo = usersDomain.createPasswordValidationInformation(password)

        return trxManager.run {
            if (repoUsers.findByEmail(email) != null) {
                return@run failure(UserError.AlreadyUsedEmailAddress)
            }
            val participant = repoUsers.createUser(name, email, passwordValidationInfo)
            success(participant)
        }
    }

    fun createToken(
        email: String,
        password: String,
    ): Either<TokenCreationError, TokenExternalInfo> {
        if (email.isBlank() || password.isBlank()) {
            failure(TokenCreationError.UserOrPasswordAreInvalid)
        }
        return trxManager.run {
            val user: User =
                repoUsers.findByEmail(email)
                    ?: return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            if (!usersDomain.validatePassword(password, user.passwordValidation)) {
                return@run failure(TokenCreationError.UserOrPasswordAreInvalid)
            }
            val tokenValue = usersDomain.generateTokenValue()
            val now = clock.now()
            val newToken =
                Token(
                    usersDomain.createTokenValidationInformation(tokenValue),
                    user.id,
                    createdAt = now,
                    lastUsedAt = now,
                )
            repoUsers.createToken(newToken, usersDomain.maxNumberOfTokensPerUser)
            Either.Right(
                TokenExternalInfo(
                    tokenValue,
                    usersDomain.getTokenExpiration(newToken),
                ),
            )
        }
    }

    fun revokeToken(token: String): Boolean {
        val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
        return trxManager.run {
            repoUsers.removeTokenByValidationInfo(tokenValidationInfo)
            true
        }
    }

    fun getUserByToken(token: String): User? {
        if (!usersDomain.canBeToken(token)) {
            return null
        }
        return trxManager.run {
            val tokenValidationInfo = usersDomain.createTokenValidationInformation(token)
            val userAndToken: Pair<User, Token>? = repoUsers.getTokenByTokenValidationInfo(tokenValidationInfo)
            if (userAndToken != null && usersDomain.isTokenTimeValid(clock, userAndToken.second)) {
                repoUsers.updateTokenLastUsed(userAndToken.second, clock.now())
                userAndToken.first
            } else {
                null
            }
        }
    }
}
