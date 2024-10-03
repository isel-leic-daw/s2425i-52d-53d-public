package pt.isel.mem

import jakarta.inject.Named
import pt.isel.Participant
import pt.isel.ParticipantKind
import pt.isel.RepositoryParticipant

/**
 * Naif in memory repository non thread-safe and basic sequential id.
 * Useful for unit tests purpose.
 */
@Named
class RepositoryParticipantInMem : RepositoryParticipant {
    private val participants = mutableListOf<Participant>()

    override fun createParticipant(name: String, email: String, kind: ParticipantKind): Participant {
        return Participant(participants.count(), name, email, kind)
            .also { participants.add(it) }
    }

    override fun findByEmail(email: String): Participant? {
        return participants.firstOrNull { it.email == email }
    }

    override fun findById(id: Int): Participant? {
        return participants.firstOrNull { it.id == id }
    }

    override fun findAll(): List<Participant> {
        return participants.toList()
    }

    override fun save(entity: Participant) {
        participants.removeIf { it.id == entity.id }
        participants.add(entity)
    }

    override fun deleteById(id: Int) {
        participants.removeIf { it.id == id }
    }

    override fun clear() { participants.clear() }
}
