package pt.isel.mem

import pt.isel.RepositoryUser
import pt.isel.User

class RepositoryUserInMem : RepositoryUser {
    val users = mutableListOf<User>()

    override fun createUser(name: String, email: String): User {
        return User(users.size, name = name, email = email)
            .also { users.add(it) }
    }

    override fun findByEmail(email: String): User? {
        return users.firstOrNull { it.email == email }
    }

    override fun findById(id: Int): User? {
        return users.firstOrNull { it.id == id }
    }

    override fun findAll(): List<User> {
        return users.toList()
    }

    override fun save(entity: User) {
        users.removeIf { it.id == entity.id }
        users.add(entity)
    }

    override fun deleteById(id: Int) {
        users.removeIf { it.id == id }
    }

    override fun clear() { users.clear() }
}