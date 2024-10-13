package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet

class RepositoryParticipantJdbi(
    private val handle: Handle,
) : RepositoryParticipant {
    private val repoUsers = RepositoryUserJdbi(handle)

    override fun findById(id: Int): Participant? =
        handle
            .createQuery(
                """
                SELECT p.id, u.id as user_id, u.name, u.email, ts.id as time_slot_id, ts.start_time, 
                       ts.duration_in_minutes, e.id as event_id, e.title, e.description, e.organizer_id, 
                       e.selection_type
                FROM dbo.participants p
                JOIN dbo.users u ON p.user_id = u.id
                JOIN dbo.time_slots ts ON p.time_slot_multiple_id = ts.id
                JOIN dbo.events e ON ts.event_id = e.id
                WHERE p.id = :id
                """,
            ).bind("id", id)
            .map { rs, _ -> mapRowToParticipant(rs) }
            .findOne()
            .orElse(null)

    override fun findAll(): List<Participant> =
        handle
            .createQuery(
                """
                SELECT p.id, u.id as user_id, u.name, u.email, ts.id as time_slot_id, ts.start_time, 
                       ts.duration_in_minutes, e.id as event_id, e.title, e.description, e.organizer_id, 
                       e.selection_type
                FROM dbo.participants p
                JOIN dbo.users u ON p.user_id = u.id
                JOIN dbo.time_slots ts ON p.time_slot_multiple_id = ts.id
                JOIN dbo.events e ON ts.event_id = e.id
                """,
            ).map { rs, _ -> mapRowToParticipant(rs) }
            .list()

    override fun save(entity: Participant) {
        handle
            .createUpdate(
                """
            UPDATE dbo.participants 
            SET user_id = :user_id, slot_id = :slot_id
            WHERE id = :id
            """,
            ).bind("id", entity.id)
            .bind("user_id", entity.user.id)
            .bind("slot_id", entity.slot.id)
            .execute()
    }

    override fun deleteById(id: Int) {
        handle
            .createUpdate("DELETE FROM dbo.participants WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.participants").execute()
    }

    override fun createParticipant(
        user: User,
        slot: TimeSlotMultiple,
    ): Participant {
        val participantId =
            handle
                .createUpdate(
                    """
                INSERT INTO dbo.participants (user_id, time_slot_multiple_id)
                VALUES (:userId, :timeSlotId)
                """,
                ).bind("userId", user.id)
                .bind("timeSlotId", slot.id)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        return Participant(
            id = participantId,
            user = user,
            slot = slot,
        )
    }

    override fun findByEmail(
        email: String,
        slot: TimeSlotMultiple,
    ): Participant? =
        handle
            .createQuery(
                """
            SELECT p.*, 
                u.id AS user_id, u.name, u.email, 
                ts.*, e.* FROM dbo.participants p
            JOIN dbo.users u ON p.user_id = u.id
            JOIN dbo.time_slots ts ON p.time_slot_multiple_id = ts.id
            JOIN dbo.events e ON ts.event_id = e.id
            WHERE u.email = :email AND ts.id = :slot_id
            """,
            ).bind("email", email)
            .bind("slot_id", slot.id)
            .map { rs, _ -> mapRowToParticipant(rs) }
            .findOne()
            .orElse(null)

    override fun findAllByTimeSlot(slot: TimeSlotMultiple): List<Participant> =
        handle
            .createQuery(
                """
            SELECT p.*, u.*, ts.*, e.* FROM dbo.participants p
            JOIN dbo.users u ON p.user_id = u.id
            JOIN dbo.time_slots ts ON p.time_slot_multiple_id = ts.id
            JOIN dbo.events e ON ts.event_id = e.id
            WHERE ts.id = :slot_id
            """,
            ).bind("slot_id", slot.id)
            .map { rs, _ -> mapRowToParticipant(rs) }
            .list()

    private fun mapRowToParticipant(rs: ResultSet): Participant {
        val user =
            User(
                id = rs.getInt("user_id"),
                name = rs.getString("name"),
                email = rs.getString("email"),
                PasswordValidationInfo(rs.getString("password_validation")),
            )
        val organizer = repoUsers.findById(rs.getInt("organizer_id"))
        requireNotNull(organizer)
        val event =
            Event(
                id = rs.getInt("event_id"),
                title = rs.getString("title"),
                description = rs.getString("description"),
                organizer = organizer,
                selectionType = SelectionType.valueOf(rs.getString("selection_type")),
            )
        val timeSlot =
            TimeSlotMultiple(
                id = rs.getInt("time_slot_multiple_id"),
                startTime = rs.getTimestamp("start_time").toLocalDateTime(),
                durationInMinutes = rs.getInt("duration_in_minutes"),
                event = event,
            )
        return Participant(id = rs.getInt("id"), user = user, slot = timeSlot)
    }
}
