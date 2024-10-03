package pt.isel.mem

import jakarta.inject.Named
import pt.isel.*

@Named
class TransactionManagerInMem : TransactionManager {
    val repoEvents = RepositoryEventInMem()
    val repoParticipants = RepositoryParticipantInMem()
    val repoSlots = RepositoryTimeslotInMem()


    override fun <R> run(block: Transaction.() -> R): R {
        return block(TransactionInMem(repoEvents, repoParticipants, repoSlots))
    }
}