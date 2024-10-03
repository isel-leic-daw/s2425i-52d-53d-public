package pt.isel

/**
 * Repository interface for managing events, extends the generic Repository
 */
interface RepositoryEvent : Repository<Event> {
    fun createEvent(
        title: String,
        description: String?,
        organizer: Participant,
        selectionType: SelectionType
    ) : Event
}