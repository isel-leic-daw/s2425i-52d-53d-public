package pt.isel

import java.time.LocalDateTime

/**
 * Repository interface for managing time slots, extends the generic Repository
 */
interface RepositoryTimeSlot : Repository<TimeSlot> {
    fun createTimeSlotSingle(eventId: Int, startTime: LocalDateTime, durationInMinutes: Int): TimeSlotSingle

    fun createTimeSlotMultiple(eventId: Int, startTime: LocalDateTime, durationInMinutes: Int): TimeSlotMultiple
}