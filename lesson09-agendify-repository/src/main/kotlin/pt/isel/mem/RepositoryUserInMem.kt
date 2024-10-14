package pt.isel.mem

import kotlinx.datetime.Instant
import pt.isel.PasswordValidationInfo
import pt.isel.RepositoryUser
import pt.isel.Token
import pt.isel.TokenValidationInfo
import pt.isel.User

class RepositoryUserInMem : RepositoryUser {
    private val users = mutableListOf<User>()
    private val tokens = mutableListOf<Token>()

    override fun createUser(
        name: String,
        email: String,
        passwordValidation: PasswordValidationInfo,
    ): User = User(users.size, name, email, passwordValidation).also { users.add(it) }

    override fun findByEmail(email: String): User? = users.firstOrNull { it.email == email }

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        tokens.firstOrNull { it.tokenValidationInfo == tokenValidationInfo }?.let {
            val user = findById(it.userId)
            requireNotNull(user)
            user to it
        }

    override fun createToken(
        token: Token,
        maxTokens: Int,
    ) {
        val nrOfTokens = tokens.count { it.userId == token.userId }

        // Remove the oldest token if we have achieved the maximum number of tokens
        if (nrOfTokens >= maxTokens) {
            tokens
                .filter { it.userId == token.userId }
                .minByOrNull { it.lastUsedAt }!!
                .also { tk -> tokens.removeIf { it.tokenValidationInfo == tk.tokenValidationInfo } }
        }
        tokens.add(token)
    }

    override fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    ) {
        tokens.removeIf { it.tokenValidationInfo == token.tokenValidationInfo }
        tokens.add(token)
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        val count = tokens.count { it.tokenValidationInfo == tokenValidationInfo }
        tokens.removeAll { it.tokenValidationInfo == tokenValidationInfo }
        return count
    }

    override fun findById(id: Int): User? = users.firstOrNull { it.id == id }

    override fun findAll(): List<User> = users.toList()

    override fun save(entity: User) {
        users.removeIf { it.id == entity.id }
        users.add(entity)
    }

    override fun deleteById(id: Int) {
        users.removeIf { it.id == id }
    }

    override fun clear() {
        tokens.clear()
        users.clear()
    }
}
