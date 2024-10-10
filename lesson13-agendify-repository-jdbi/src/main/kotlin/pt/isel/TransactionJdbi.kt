package pt.isel

import org.jdbi.v3.core.Handle

class TransactionJdbi(
    private val handle: Handle,
) : Transaction {
    override val repoEvents = RepositoryEventJdbi(handle)
    override val repoUsers = RepositoryUserJdbi(handle)
    override val repoParticipants = RepositoryParticipantJdbi(handle)
    override val repoSlots = RepositoryTimeSlotJdbi(handle)

    override fun rollback() {
        handle.rollback()
    }
}
