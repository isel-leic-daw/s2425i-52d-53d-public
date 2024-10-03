package pt.isel.mem

import jakarta.inject.Named
import pt.isel.*
import java.time.LocalDateTime


/**
 * Naif in memory repository non thread-safe and basic sequential id.
 * Useful for unit tests purpose.
 */
@Named
class RepositoryTimeslotInMem : RepositoryTimeSlot {
    private val timeSlots = mutableListOf<TimeSlot>()

    override fun createTimeSlotSingle(startTime: LocalDateTime, durationInMinutes: Int, event: Event): TimeSlotSingle {
        return TimeSlotSingle(timeSlots.count(), startTime, durationInMinutes, event)
            .also { timeSlots.add(it) }
    }

    override fun createTimeSlotMultiple(
        startTime: LocalDateTime,
        durationInMinutes: Int,
        event: Event
    ): TimeSlotMultiple {
        return TimeSlotMultiple(timeSlots.count(), startTime, durationInMinutes, event)
            .also { timeSlots.add(it) }
    }

    override fun findAllByEvent(event: Event): List<TimeSlot> {
        return timeSlots.filter { it.event.id == event.id }
    }


    override fun findById(id: Int): TimeSlot? {
        return timeSlots.firstOrNull { it.id == id }
    }

    override fun findAll(): List<TimeSlot> {
        return timeSlots.toList()
    }

    override fun save(entity: TimeSlot) {
        timeSlots.removeIf { it.id == entity.id }
        timeSlots.add(entity)
    }

    override fun deleteById(id: Int) {
        timeSlots.removeIf { it.id == id }
    }

    override fun clear() { timeSlots.clear() }
}
