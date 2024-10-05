package pt.isel.mem

import jakarta.inject.Named
import pt.isel.*

@Named
class TransactionManagerInMem : TransactionManager {
    private val repoEvents = RepositoryEventInMem()
    private val repoUsers = RepositoryUserInMem()
    private val repoParticipants = RepositoryParticipantInMem()
    private val repoSlots = RepositoryTimeslotInMem()


    override fun <R> run(block: Transaction.() -> R): R {
        return block(TransactionInMem(repoEvents, repoUsers, repoParticipants, repoSlots))
    }
}