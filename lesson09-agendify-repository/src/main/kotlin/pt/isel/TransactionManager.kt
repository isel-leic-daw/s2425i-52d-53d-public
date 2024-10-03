package pt.isel

/**
 * Yuml class diagram

    // Define interfaces
    [TransactionManager|block: (Transaction) \-\> R]
    [<<TransactionManager>>;TransactionManagerJdbi|block: (Transaction) \-\> R]
    [<<Transaction>>;TransactionJdbi|handle: Handle]
    [<<RepositoryParticipants>>;RepositoryParticipantsJdbi|handle: Handle]
    [<<RepositoryEvents>>;RepositoryEventsJdbi|handle: Handle]
    [<<RepositoryTimeSlot>>;RepositoryTimeSlotJdbi|handle: Handle]

    // Relations
    [TransactionManager]uses-.->[Transaction]
    [TransactionManagerJdbi]new-.->[TransactionJdbi]

    [Transaction]->[RepositoryParticipants]
    [Transaction]->[RepositoryEvents]
    [Transaction]->[RepositoryTimeSlot]

    [TransactionJdbi]->[RepositoryParticipantsJdbi]
    [TransactionJdbi]->[RepositoryEventsJdbi]
    [TransactionJdbi]->[RepositoryTimeSlotJdbi]
 */
interface TransactionManager {
    /**
     * This method creates an instance of Transaction, potentially
     * initializing a JDBC Connection,a JDBI Handle, or another resource,
     * which is then passed as an argument to the Transaction constructor.
     */
    fun <R> run(block: Transaction.() -> R) : R
}