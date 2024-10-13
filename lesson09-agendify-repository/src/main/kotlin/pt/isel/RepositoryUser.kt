package pt.isel

/**
 * Repository interface for managing users, extends the generic Repository
 */
interface RepositoryUser : Repository<User> {
    fun createUser(
        name: String,
        email: String,
        passwordValidation: PasswordValidationInfo,
    ): User

    fun findByEmail(email: String): User?
}
