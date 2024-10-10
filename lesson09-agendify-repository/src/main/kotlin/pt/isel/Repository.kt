package pt.isel

/**
 * Generic repository interface for basic CRUD operations
 */
interface Repository<T> {
    fun findById(id: Int): T? // Find an entity by its ID

    fun findAll(): List<T> // Retrieve all entities

    fun save(entity: T) // Save a new or existing entity

    fun deleteById(id: Int) // Delete an entity by its ID

    fun clear() // Delete all entries
}
