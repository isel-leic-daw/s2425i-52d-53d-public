package pt.isel

import org.jdbi.v3.core.Handle
import java.sql.ResultSet
import java.time.LocalDateTime

class RepositoryTimeSlotJdbi(
    private val handle: Handle,
) : RepositoryTimeSlot {
    private val repoEvents = RepositoryEventJdbi(handle)

    override fun createTimeSlotSingle(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotSingle {
        val timeSlotId =
            handle
                .createUpdate(
                    """
                INSERT INTO dbo.time_slots (start_time, duration_in_minutes, event_id)
                VALUES (:startTime, :durationInMinutes, :eventId)
                """,
                ).bind("startTime", startTime)
                .bind("durationInMinutes", durationInMinutes)
                .bind("eventId", event.id)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        handle
            .createUpdate(
                """
                INSERT INTO dbo.time_slot_singles (time_slot_id, owner_id)
                VALUES (:timeSlotId, NULL)
                """,
            ).bind("timeSlotId", timeSlotId)
            .execute()

        return TimeSlotSingle(
            id = timeSlotId,
            startTime = startTime,
            durationInMinutes = durationInMinutes,
            event = event,
            owner = null,
        )
    }

    override fun createTimeSlotMultiple(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotMultiple {
        val timeSlotId =
            handle
                .createUpdate(
                    """
                INSERT INTO dbo.time_slots (start_time, duration_in_minutes, event_id)
                VALUES (:startTime, :durationInMinutes, :eventId)
                """,
                ).bind("startTime", startTime)
                .bind("durationInMinutes", durationInMinutes)
                .bind("eventId", event.id)
                .executeAndReturnGeneratedKeys()
                .mapTo(Int::class.java)
                .one()

        handle
            .createUpdate(
                """
                INSERT INTO dbo.time_slot_multiples (time_slot_id)
                VALUES (:timeSlotId)
                """,
            ).bind("timeSlotId", timeSlotId)
            .execute()

        return TimeSlotMultiple(
            id = timeSlotId,
            startTime = startTime,
            durationInMinutes = durationInMinutes,
            event = event,
        )
    }

    override fun findAllByEvent(event: Event): List<TimeSlot> {
        val timeSlots = mutableListOf<TimeSlot>()

        // Fetch TimeSlotSingle entries
        val singleSlots =
            handle
                .createQuery(
                    """
                SELECT ts.id as time_slot_id, ts.start_time, ts.duration_in_minutes, ts.event_id, 
                       tss.owner_id, u.id as owner_user_id, u.name as owner_name, u.email as owner_email
                FROM dbo.time_slots ts
                LEFT JOIN dbo.time_slot_singles tss ON ts.id = tss.time_slot_id
                LEFT JOIN dbo.users u ON tss.owner_id = u.id
                WHERE ts.event_id = :eventId
                """,
                ).bind("eventId", event.id)
                .map { rs, _ -> mapRowToTimeSlot(rs, event) }
                .list()

        timeSlots.addAll(singleSlots)

        // Fetch TimeSlotMultiple entries
        val multipleSlots =
            handle
                .createQuery(
                    """
                SELECT ts.id as time_slot_id, ts.start_time, ts.duration_in_minutes, ts.event_id
                FROM dbo.time_slot_multiples tsm
                LEFT JOIN dbo.time_slots ts ON ts.id = tsm.time_slot_id
                WHERE ts.event_id = :eventId
                """,
                ).bind("eventId", event.id)
                .map { rs, _ ->
                    TimeSlotMultiple(
                        id = rs.getInt("time_slot_id"),
                        startTime = rs.getTimestamp("start_time").toLocalDateTime(),
                        durationInMinutes = rs.getInt("duration_in_minutes"),
                        event = event,
                    )
                }.list()

        timeSlots.addAll(multipleSlots)

        return timeSlots
    }

    private fun mapRowToTimeSlot(
        rs: ResultSet,
        event: Event,
    ): TimeSlotSingle {
        val owner =
            if (rs.getInt("owner_id") != 0) {
                User(
                    id = rs.getInt("owner_user_id"),
                    name = rs.getString("owner_name"),
                    email = rs.getString("owner_email"),
                    passwordValidation = PasswordValidationInfo(rs.getString("password_validation")),
                )
            } else {
                null
            }
        return TimeSlotSingle(
            id = rs.getInt("time_slot_id"),
            startTime = rs.getTimestamp("start_time").toLocalDateTime(),
            durationInMinutes = rs.getInt("duration_in_minutes"),
            event = event,
            owner = owner,
        )
    }

    override fun save(entity: TimeSlot) {
        when (entity) {
            is TimeSlotSingle -> {
                handle
                    .createUpdate(
                        """
                UPDATE dbo.time_slots 
                SET start_time = :startTime, duration_in_minutes = :durationInMinutes 
                WHERE id = :id
                """,
                    ).bind("startTime", entity.startTime)
                    .bind("durationInMinutes", entity.durationInMinutes)
                    .bind("id", entity.id)
                    .execute()

                // Update owner in dbo.time_slot_singles
                handle
                    .createUpdate(
                        """
                UPDATE dbo.time_slot_singles 
                SET owner_id = :ownerId 
                WHERE time_slot_id = :timeSlotId
                """,
                    ).bind("ownerId", entity.owner?.id) // This will be null if there is no owner
                    .bind("timeSlotId", entity.id)
                    .execute()
            }

            is TimeSlotMultiple -> {
                handle
                    .createUpdate(
                        """
                UPDATE dbo.time_slots 
                SET start_time = :startTime, duration_in_minutes = :durationInMinutes 
                WHERE id = :id
                """,
                    ).bind("startTime", entity.startTime)
                    .bind("durationInMinutes", entity.durationInMinutes)
                    .bind("id", entity.id)
                    .execute()
            }
        }
    }

    override fun findById(id: Int): TimeSlot? =
        handle
            .createQuery(
                """
                SELECT ts.id as time_slot_id, ts.start_time, ts.duration_in_minutes, ts.event_id, 
                       tss.owner_id, u.id as owner_user_id, u.name as owner_name, u.email as owner_email,
                       u.password_validation as password_validation
                FROM dbo.time_slots ts
                LEFT JOIN dbo.time_slot_singles tss ON ts.id = tss.time_slot_id
                LEFT JOIN dbo.users u ON tss.owner_id = u.id
                WHERE ts.id = :timeSlotId
                """,
            ).bind("timeSlotId", id)
            .map { rs, _ ->
                val event = repoEvents.findById(rs.getInt("event_id"))
                checkNotNull(event)

                when (event.selectionType) {
                    SelectionType.SINGLE -> {
                        mapRowToTimeSlot(rs, event)
                    }

                    SelectionType.MULTIPLE -> {
                        TimeSlotMultiple(
                            id = rs.getInt("time_slot_id"),
                            startTime = rs.getTimestamp("start_time").toLocalDateTime(),
                            durationInMinutes = rs.getInt("duration_in_minutes"),
                            event = event,
                        )
                    }
                }
            }.findOne()
            .orElse(null)

    override fun findAll(): List<TimeSlot> {
        // Fetching all time slots, including both TimeSlotSingle and TimeSlotMultiple
        val timeSlots = mutableListOf<TimeSlot>()

        // Fetch TimeSlotSingle entries
        val singleSlots =
            handle
                .createQuery(
                    """
                SELECT ts.id as time_slot_id, ts.start_time, ts.duration_in_minutes, ts.event_id, 
                       tss.owner_id, u.id as owner_user_id, u.name as owner_name, u.email as owner_email,
                       u.password_validation as password_validation
                FROM dbo.time_slots ts
                LEFT JOIN dbo.time_slot_singles tss ON ts.id = tss.time_slot_id
                LEFT JOIN dbo.users u ON tss.owner_id = u.id
                """,
                ).map { rs, _ ->
                    val event = repoEvents.findById(rs.getInt("event_id"))
                    checkNotNull(event)
                    mapRowToTimeSlot(rs, event)
                }.list()

        timeSlots.addAll(singleSlots)

        // Fetch TimeSlotMultiple entries
        val multipleSlots =
            handle
                .createQuery(
                    """
                SELECT ts.id as time_slot_id, ts.start_time, ts.duration_in_minutes, ts.event_id
                FROM dbo.time_slots ts
                LEFT JOIN dbo.time_slot_multiples tsm ON ts.id = tsm.time_slot_id
                """,
                ).map { rs, _ ->
                    val event = repoEvents.findById(rs.getInt("event_id"))
                    checkNotNull(event)
                    TimeSlotMultiple(
                        id = rs.getInt("time_slot_id"),
                        startTime = rs.getTimestamp("start_time").toLocalDateTime(),
                        durationInMinutes = rs.getInt("duration_in_minutes"),
                        event = event,
                    )
                }.list()

        timeSlots.addAll(multipleSlots)

        return timeSlots
    }

    override fun deleteById(id: Int) {
        handle
            .createUpdate("DELETE FROM dbo.time_slots WHERE id = :id")
            .bind("id", id)
            .execute()
    }

    override fun clear() {
        handle.createUpdate("DELETE FROM dbo.time_slots").execute()
    }
}
