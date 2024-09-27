package pt.isel

import java.time.LocalDateTime

sealed class TimeSlot(
    open val id: Int,
    open val startTime: LocalDateTime,
    open val durationInMinutes: Int
) {
    abstract fun addParticipant(participant: Participant): TimeSlot

    abstract fun removeParticipant(participant: Participant): TimeSlot
}

/**
 * Represents a time slot available for a single participant
 */
data class TimeSlotSingle(
    override val id: Int,
    override val startTime: LocalDateTime,
    override val durationInMinutes: Int,
    val owner: Participant? = null
) : TimeSlot(id, startTime, durationInMinutes) {
    /**
     * Assign new participant if the slot is empty
     */
    override fun addParticipant(participant: Participant): TimeSlotSingle {
        check(owner == null) { "This time slot is already allocated to a participant." }
        return this.copy(owner = participant)
    }

    override fun removeParticipant(participant: Participant): TimeSlotSingle {
        check(owner != null) { "The participant ${participant.name} is not the owner of this slot and cannot be removed!" }
        require(owner == participant) { }
        return this.copy(owner = null)
    }
}

/**
 * Represents a time slot available for multiple participants
 */
data class TimeSlotMultiple(
    override val id: Int,       // Changed from UUID to Int
    override val startTime: LocalDateTime,
    override val durationInMinutes: Int,
    val participants: List<Participant> = emptyList() // List of participants who are available for this time slot
) : TimeSlot(id, startTime, durationInMinutes) {
    override fun addParticipant(participant: Participant): TimeSlotMultiple {
        return this.copy(participants = participants + participant)
    }

    override fun removeParticipant(participant: Participant): TimeSlotMultiple {
        return this.copy(participants = participants - participant)
    }
}