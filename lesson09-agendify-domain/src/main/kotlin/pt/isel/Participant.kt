package pt.isel

/**
 * Represents a participant in the event (could be organizer or guest)
 */
data class Participant(
    val id: Int,
    val name: String,
    val email: String,
    val kind: ParticipantKind
)
