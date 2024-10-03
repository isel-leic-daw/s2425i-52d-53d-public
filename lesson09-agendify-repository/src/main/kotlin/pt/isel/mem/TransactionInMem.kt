package pt.isel.mem

import pt.isel.*

class TransactionInMem(
    override val repoEvents: RepositoryEvent,
    override val repoParticipants: RepositoryParticipant,
    override val repoSlots: RepositoryTimeSlot
) : Transaction {

    override fun rollback() {
        throw UnsupportedOperationException()
    }
}