package pt.isel

import jakarta.inject.Named


sealed class ParticipantError {
    data object AlreadyUsedEmailAddress : ParticipantError()
}

@Named
class ParticipantService(
    private val trxManager: TransactionManager
) {
    /**
     * Create a new participant
     */
    fun createParticipant(
        name: String,
        email: String,
        kind: ParticipantKind
    ): Either<ParticipantError.AlreadyUsedEmailAddress, Participant> = trxManager.run {
        if(repoParticipants.findByEmail(email) != null) {
            return@run failure(ParticipantError.AlreadyUsedEmailAddress)
        }
        val participant = repoParticipants.createParticipant(name, email, kind)
        success(participant)
    }
}