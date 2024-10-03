package pt.isel

import jakarta.inject.Named
import java.time.LocalDateTime

sealed class EventError {
    data object EventNotFound : EventError()
    data object ParticipantNotFound : EventError()
    data object TimeSlotNotFound : EventError()
    data object SingleTimeSlotAlreadyAllocated : EventError()
}

@Named
class EventService(
    private val trxManager: TransactionManager
) {

    /**
     * Add participant to a time slot
     */
    fun addParticipantToTimeSlot(timeSlotId: Int, participantId: Int): Either<EventError, TimeSlot> = trxManager.run {
        // Find the time slot within the event
        val timeSlot: TimeSlot = repoSlots.findById(timeSlotId)
            ?: return@run failure(EventError.TimeSlotNotFound)

        // For
        if(timeSlot is TimeSlotSingle && timeSlot.owner != null) {
            return@run failure(EventError.SingleTimeSlotAlreadyAllocated)
        }
        // Fetch the participant
        val participant = repoParticipants.findById(participantId)
            ?: return@run failure(EventError.ParticipantNotFound)

        // Try to add the participant to the time slot
        val updatedTimeSlot = timeSlot.addParticipant(participant)

        // Replace the old time slot with the updated one in the event
        repoSlots.save(updatedTimeSlot)

        // Return the updated event in the Either type
        success(updatedTimeSlot)
    }

    /**
     * Create a free time slot based on event's selection type
     */
    fun createFreeTimeSlot(
        eventId: Int,
        startTime: LocalDateTime,
        durationInMinutes: Int
    ): Either<EventError.EventNotFound, TimeSlot> = trxManager.run {
        val event = repoEvents.findById(eventId) ?: return@run failure(EventError.EventNotFound)

        // Determine TimeSlot type based on Event's selection type and create it on Repository
        val timeSlot = when (event.selectionType) {
            SelectionType.SINGLE -> repoSlots.createTimeSlotSingle(startTime, durationInMinutes, event)
            SelectionType.MULTIPLE -> repoSlots.createTimeSlotMultiple(startTime, durationInMinutes, event)
        }
        return@run success(timeSlot)
    }

    fun getAllEvents(): List<Event> = trxManager.run { repoEvents.findAll() }

    fun getEventById(eventId: Int): Either<EventError.EventNotFound, Event> = trxManager.run {
        repoEvents
            .findById(eventId)
            ?.let { success(it) }
            ?: failure(EventError.EventNotFound)
    }

    fun createEvent(
        title: String,
        description: String?,
        organizerId: Int,
        selectionType: SelectionType
    ): Either<EventError.ParticipantNotFound, Event> = trxManager.run {
        val organizer = repoParticipants.findById(organizerId) ?: return@run failure(EventError.ParticipantNotFound)
        success(repoEvents.createEvent(title, description, organizer, selectionType))
    }
}
