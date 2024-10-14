package pt.isel

import kotlinx.datetime.Instant
import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.kotlin.mapTo
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import org.slf4j.LoggerFactory
import java.sql.ResultSet

class RepositoryUserJdbi(
    private val handle: Handle,
) : RepositoryUser {
    override fun findById(id: Int): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .map(UserMapper())
            .findOne()
            .orElse(null)

    override fun findAll(): List<User> =
        handle
            .createQuery("SELECT * FROM dbo.users")
            .map(UserMapper())
            .list()

    override fun save(entity: User) {
        handle
            .createUpdate(
                """
            UPDATE dbo.users 
            SET name = :name, email = :email 
            WHERE id = :id
            """,
            ).bind("name", entity.name)
            .bind("email", entity.email)
            .bind("id", entity.id)
            .execute()
    }

    override fun deleteById(id: Int) {
        handle
            .createUpdate("DELETE FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.Tokens").execute()
        handle.createUpdate("DELETE FROM dbo.users").execute()
    }

    override fun createUser(
        name: String,
        email: String,
        passwordValidation: PasswordValidationInfo,
    ): User {
        val id =
            handle
                .createUpdate(
                    """
            INSERT INTO dbo.users (name, email, password_validation) 
            VALUES (:name, :email, :password_validation)
            RETURNING id
            """,
                ).bind("name", name)
                .bind("email", email)
                .bind("password_validation", passwordValidation.validationInfo)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return User(id, name, email, passwordValidation)
    }

    override fun findByEmail(email: String): User? =
        handle
            .createQuery("SELECT * FROM dbo.users WHERE email = :email")
            .bind("email", email)
            .map(UserMapper())
            .findOne()
            .orElse(null)

    override fun getTokenByTokenValidationInfo(tokenValidationInfo: TokenValidationInfo): Pair<User, Token>? =
        handle
            .createQuery(
                """
                select id, name, email, password_validation, token_validation, created_at, last_used_at
                from dbo.Users as users 
                inner join dbo.Tokens as tokens 
                on users.id = tokens.user_id
                where token_validation = :validation_information
            """,
            ).bind("validation_information", tokenValidationInfo.validationInfo)
            .mapTo<UserAndTokenModel>()
            .singleOrNull()
            ?.userAndToken

    override fun createToken(
        token: Token,
        maxTokens: Int,
    ) {
        // Delete the oldest token when achieved the maximum number of tokens
        val deletions =
            handle
                .createUpdate(
                    """
                    delete from dbo.Tokens 
                    where user_id = :user_id 
                        and token_validation in (
                            select token_validation from dbo.Tokens where user_id = :user_id 
                                order by last_used_at desc offset :offset
                        )
                    """.trimIndent(),
                ).bind("user_id", token.userId)
                .bind("offset", maxTokens - 1)
                .execute()

        logger.info("{} tokens deleted when creating new token", deletions)

        handle
            .createUpdate(
                """
                insert into dbo.Tokens(user_id, token_validation, created_at, last_used_at) 
                values (:user_id, :token_validation, :created_at, :last_used_at)
                """.trimIndent(),
            ).bind("user_id", token.userId)
            .bind("token_validation", token.tokenValidationInfo.validationInfo)
            .bind("created_at", token.createdAt.epochSeconds)
            .bind("last_used_at", token.lastUsedAt.epochSeconds)
            .execute()
    }

    override fun updateTokenLastUsed(
        token: Token,
        now: Instant,
    ) {
        handle
            .createUpdate(
                """
                update dbo.Tokens
                set last_used_at = :last_used_at
                where token_validation = :validation_information
                """.trimIndent(),
            ).bind("last_used_at", now.epochSeconds)
            .bind("validation_information", token.tokenValidationInfo.validationInfo)
            .execute()
    }

    override fun removeTokenByValidationInfo(tokenValidationInfo: TokenValidationInfo): Int {
        return handle.createUpdate(
            """
                delete from dbo.Tokens
                where token_validation = :validation_information
            """,
        )
            .bind("validation_information", tokenValidationInfo.validationInfo)
            .execute()
    }

    // Mapper for User
    private class UserMapper : RowMapper<User> {
        override fun map(
            rs: ResultSet,
            ctx: StatementContext,
        ): User =
            User(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                email = rs.getString("email"),
                passwordValidation = PasswordValidationInfo(rs.getString("password_validation")),
            )
    }

    private data class UserAndTokenModel(
        val id: Int,
        val name: String,
        val email: String,
        val passwordValidation: PasswordValidationInfo,
        val tokenValidation: TokenValidationInfo,
        val createdAt: Long,
        val lastUsedAt: Long,
    ) {
        val userAndToken: Pair<User, Token>
            get() =
                Pair(
                    User(id, name, email, passwordValidation),
                    Token(
                        tokenValidation,
                        id,
                        Instant.fromEpochSeconds(createdAt),
                        Instant.fromEpochSeconds(lastUsedAt),
                    ),
                )
    }

    companion object {
        private val logger = LoggerFactory.getLogger(RepositoryUserJdbi::class.java)
    }
}
