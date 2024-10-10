package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet

class RepositoryEventJdbi(
    private val handle: Handle,
) : RepositoryEvent {
    override fun findById(id: Int): Event? =
        handle
            .createQuery(
                """
            SELECT e.*, u.* FROM dbo.events e
            JOIN dbo.users u ON e.organizer_id = u.id
            WHERE e.id = :id
            """,
            ).bind("id", id)
            .map { rs, _ -> mapRowToEvent(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<Event> =
        handle
            .createQuery(
                """
            SELECT e.*, u.* FROM dbo.events e
            JOIN dbo.users u ON e.organizer_id = u.id
            """,
            ).map { rs, _ -> mapRowToEvent(rs) }
            .list()

    override fun save(entity: Event) {
        handle
            .createUpdate(
                """
            UPDATE dbo.events 
            SET title = :title, description = :description, organizer_id = :organizer_id, selection_type = :selection_type
            WHERE id = :id
            """,
            ).bind("id", entity.id)
            .bind("title", entity.title)
            .bind("description", entity.description)
            .bind("organizer_id", entity.organizer.id)
            .bind("selection_type", entity.selectionType.name)
            .execute()
    }

    override fun deleteById(id: Int) {
        handle
            .createUpdate("DELETE FROM dbo.events WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.events").execute()
    }

    override fun createEvent(
        title: String,
        description: String?,
        organizer: User,
        selectionType: SelectionType,
    ): Event {
        val id =
            handle
                .createUpdate(
                    """
            INSERT INTO dbo.events (title, description, organizer_id, selection_type) 
            VALUES (:title, :description, :organizer_id, :selection_type)
            """,
                ).bind("title", title)
                .bind("description", description)
                .bind("organizer_id", organizer.id)
                .bind("selection_type", selectionType.name)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return Event(id, title, description, organizer, selectionType)
    }

    private fun mapRowToEvent(rs: ResultSet): Event {
        val organizer = User(rs.getInt("organizer_id"), rs.getString("name"), rs.getString("email"))
        return Event(
            id = rs.getInt("id"),
            title = rs.getString("title"),
            description = rs.getString("description"),
            organizer = organizer,
            selectionType = SelectionType.valueOf(rs.getString("selection_type")),
        )
    }
}
