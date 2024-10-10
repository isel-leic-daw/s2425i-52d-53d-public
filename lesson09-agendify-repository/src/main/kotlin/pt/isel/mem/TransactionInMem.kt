package pt.isel.mem

import pt.isel.RepositoryEvent
import pt.isel.RepositoryParticipant
import pt.isel.RepositoryTimeSlot
import pt.isel.RepositoryUser
import pt.isel.Transaction

class TransactionInMem(
    override val repoEvents: RepositoryEvent,
    override val repoUsers: RepositoryUser,
    override val repoParticipants: RepositoryParticipant,
    override val repoSlots: RepositoryTimeSlot,
) : Transaction {
    override fun rollback(): Unit = throw UnsupportedOperationException()
}
