package pt.isel

/**
 * The lifecycle of a Transaction is managed outside the scope of the IoC/DI container.
 * Transactions are instantiated by a TransactionManager,
 * which is managed by the IoC/DI container (e.g., Spring).
 * The implementation of Transaction is responsible for creating the
 * necessary repository instances in its constructor.
 */
interface Transaction {
    val repoEvents: RepositoryEvent
    val repoUsers: RepositoryUser
    val repoParticipants: RepositoryParticipant
    val repoSlots: RepositoryTimeSlot

    fun rollback()
}