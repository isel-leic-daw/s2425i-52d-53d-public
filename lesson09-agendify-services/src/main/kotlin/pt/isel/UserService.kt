package pt.isel

import jakarta.inject.Named

sealed class UserError {
    data object AlreadyUsedEmailAddress : UserError()

    data object InsecurePassword : UserError()
}

@Named
class UserService(
    private val trxManager: TransactionManager,
    private val usersDomain: UsersDomain,
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
}
