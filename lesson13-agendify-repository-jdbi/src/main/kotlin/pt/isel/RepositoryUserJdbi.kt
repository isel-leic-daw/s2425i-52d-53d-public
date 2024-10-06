package pt.isel

import org.jdbi.v3.core.Handle
import org.jdbi.v3.core.mapper.RowMapper
import org.jdbi.v3.core.statement.StatementContext
import java.sql.ResultSet

class RepositoryUserJdbi(private val handle: Handle) : RepositoryUser {

    override fun findById(id: Int): User? {
        return handle.createQuery("SELECT * FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .map(UserMapper())
            .findOne()
            .orElse(null)
    }

    override fun findAll(): List<User> {
        return handle.createQuery("SELECT * FROM dbo.users")
            .map(UserMapper())
            .list()
    }

    override fun save(entity: User) {
        handle.createUpdate(
            """
            UPDATE dbo.users 
            SET name = :name, email = :email 
            WHERE id = :id
            """
        )
            .bind("name", entity.name)
            .bind("email", entity.email)
            .bind("id", entity.id)
            .execute()
    }

    override fun deleteById(id: Int) {
        handle.createUpdate("DELETE FROM dbo.users WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.users").execute()
    }

    override fun createUser(name: String, email: String): User {
        val id = handle.createUpdate(
            """
            INSERT INTO dbo.users (name, email) 
            VALUES (:name, :email)
            RETURNING id
            """
        )
            .bind("name", name)
            .bind("email", email)
            .executeAndReturnGeneratedKeys()
            .mapTo(Int::class.java)
            .one()

        return User(id, name, email)
    }

    override fun findByEmail(email: String): User? {
        return handle.createQuery("SELECT * FROM dbo.users WHERE email = :email")
            .bind("email", email)
            .map(UserMapper())
            .findOne()
            .orElse(null)
    }

    // Mapper for User
    private class UserMapper : RowMapper<User> {
        override fun map(rs: ResultSet, ctx: StatementContext): User {
            return User(
                id = rs.getInt("id"),
                name = rs.getString("name"),
                email = rs.getString("email")
            )
        }
    }
}
