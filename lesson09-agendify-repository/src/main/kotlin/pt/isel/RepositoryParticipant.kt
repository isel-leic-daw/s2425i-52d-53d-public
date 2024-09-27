package pt.isel

/**
 * Repository interface for managing participants, extends the generic Repository
 */
interface RepositoryParticipant : Repository<Participant> {
    fun createParticipant(name: String, email: String, kind: ParticipantKind) : Participant
}