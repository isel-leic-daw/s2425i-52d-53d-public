package pt.isel

import jakarta.inject.Named


sealed class UserError {
    data object AlreadyUsedEmailAddress : UserError()
}

@Named
class UserService(
    private val trxManager: TransactionManager
) {
    /**
     * Create a new participant
     */
    fun createUser(
        name: String,
        email: String,
    ): Either<UserError.AlreadyUsedEmailAddress, User> = trxManager.run {
        if(repoUsers.findByEmail(email) != null) {
            return@run failure(UserError.AlreadyUsedEmailAddress)
        }
        val participant = repoUsers.createUser(name, email)
        success(participant)
    }
}