package pt.isel.mem

import pt.isel.Transaction
import pt.isel.TransactionManager

class TransactionManagerInMem : TransactionManager {
    private val repoEvents = RepositoryEventInMem()
    private val repoUsers = RepositoryUserInMem()
    private val repoParticipants = RepositoryParticipantInMem()
    private val repoSlots = RepositoryTimeslotInMem()

    override fun <R> run(block: Transaction.() -> R): R = block(TransactionInMem(repoEvents, repoUsers, repoParticipants, repoSlots))
}
