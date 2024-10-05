package pt.isel

/**
 * Represents a participant in a TimeSlotMultiple
 */
data class Participant(
    val id: Int,
    val user: User,
    val slot: TimeSlotMultiple
)
