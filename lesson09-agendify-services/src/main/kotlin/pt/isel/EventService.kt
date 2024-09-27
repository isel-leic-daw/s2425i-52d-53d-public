package pt.isel

import java.time.LocalDateTime

sealed class EventError {
    data object EventNotFound : EventError()
    data object ParticipantNotFound : EventError()
    data object TimeSlotNotFound : EventError()
    data object SingleTimeSlotAlreadyAllocated : EventError()
}


class EventService(
    private val eventRepository: RepositoryEvent,
    private val participantRepository: RepositoryParticipant,
    private val timeSlotRepository: RepositoryTimeSlot
) {
    /**
     * Add participant to a time slot
     */
    fun addParticipantToTimeSlot(eventId: Int, timeSlotId: Int, participantId: Int): Either<EventError, Event> {
        // Fetch the event
        val event = eventRepository.findById(eventId)
            ?: return failure(EventError.EventNotFound)

        // Fetch the participant
        val participant = participantRepository.findById(participantId)
            ?: return failure(EventError.ParticipantNotFound)

        // Find the time slot within the event
        val timeSlot = event.timeSlots.find { it.id == timeSlotId }
            ?: return failure(EventError.TimeSlotNotFound)

        // For
        if(timeSlot is TimeSlotSingle && timeSlot.owner != null) {
            return failure(EventError.SingleTimeSlotAlreadyAllocated)
        }

        // Try to add the participant to the time slot
        val updatedTimeSlot = timeSlot.addParticipant(participant)

        // Replace the old time slot with the updated one in the event
        val updatedEvent = event.replaceSlot(timeSlotId, updatedTimeSlot)

        // Persist the updated time slot
        // !!!! TODO use a transaction manager to perform both operations in a single transaction.
        timeSlotRepository.save(updatedTimeSlot)
        // eventRepository.save(updatedEvent) // No FK nothing to update here on Repository

        // Return the updated event in the Either type
        return success(updatedEvent)
    }

    /**
     * Create a new participant and associate with the event
     */
    fun createParticipant(name: String, email: String, kind: ParticipantKind): Either<EventError, Participant> {
        val participant = participantRepository.createParticipant(name, email, kind)
        return success(participant)
    }
    /**
     * Create a free time slot based on event's selection type
     */
    fun createFreeTimeSlot(eventId: Int, startTime: LocalDateTime, durationInMinutes: Int): Either<EventError, TimeSlot> {
        val event = eventRepository.findById(eventId) ?: return failure(EventError.EventNotFound)

        // Determine TimeSlot type based on Event's selection type and create it on Repository
        val timeSlot = when (event.selectionType) {
            SelectionType.SINGLE -> timeSlotRepository.createTimeSlotSingle(event.id, startTime, durationInMinutes)
            SelectionType.MULTIPLE -> timeSlotRepository.createTimeSlotMultiple(event.id, startTime, durationInMinutes)
        }
        return success(timeSlot)
    }
}
