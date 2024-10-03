package pt.isel.mem

import jakarta.inject.Named
import pt.isel.Event
import pt.isel.Participant
import pt.isel.RepositoryEvent
import pt.isel.SelectionType

/**
 * Naif in memory repository non thread-safe and basic sequential id.
 * Useful for unit tests purpose.
 */
@Named
class RepositoryEventInMem : RepositoryEvent {

    private val events = mutableListOf<Event>()

    override fun createEvent(
        title: String,
        description: String?,
        organizer: Participant,
        selectionType: SelectionType
    ): Event {
        return Event(events.count(), title, description, organizer, selectionType)
            .also { events.add(it) }
    }


    override fun findById(id: Int): Event? {
        return events.firstOrNull { it.id == id }
    }

    override fun findAll(): List<Event> {
        return events.toList()
    }

    override fun save(entity: Event) {
        events.removeIf { it.id == entity.id }
        events.add(entity)
    }

    override fun deleteById(id: Int) {
        events.removeIf { it.id == id }
    }

    override fun clear() { events.clear() }
}
