package pt.isel

import java.time.LocalDateTime

/**
 * Repository interface for managing time slots, extends the generic Repository
 */
interface RepositoryTimeSlot : Repository<TimeSlot> {
    fun createTimeSlotSingle(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotSingle

    fun createTimeSlotMultiple(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event,
    ): TimeSlotMultiple

    fun findAllByEvent(event: Event): List<TimeSlot>
}
