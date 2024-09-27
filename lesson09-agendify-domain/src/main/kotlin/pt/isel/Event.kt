package pt.isel

/**
 * Represents a scheduling event
 *
 * Class diagram in yuml.me as:
[Event|+id: Int;+title: String;+description: String;+organizer: Participant;+selectionType: SelectionType;+timeSlots: List<TimeSlot>|+addTimeSlot(timeSlot: TimeSlot): Event; +replaceSlot(slotId: Int
newSlot: TimeSlot): Event]
[Participant|+id: Int;+name: String;+email: String;+kind: ParticipantKind|]
[TimeSlot|+id: Int;+startTime: LocalDateTime;+durationInMinutes: Int|+addParticipant(participant: Participant): TimeSlot; +removeParticipant(participant: Participant): TimeSlot]
[TimeSlotSingle|+owner: Participant?|]
[TimeSlotMultiple|+participants: List\<Participant\>|]

// Define relationships
[Event]->*[TimeSlot]
[TimeSlotSingle]-^[TimeSlot]
[TimeSlotSingle]->[Participant]
[TimeSlotMultiple]-^[TimeSlot]
[TimeSlotMultiple]->*[Participant]
[Participant]->[ParticipantKind]
 */
data class Event(
    val id: Int,
    val title: String,
    val description: String?,
    val organizer: Participant,
    val selectionType: SelectionType, // Indicates whether the event allows single or multiple selections
    val timeSlots: List<TimeSlot>,
) {
    fun addTimeSlot(timeSlot: TimeSlot): Event {
        return this.copy(timeSlots = timeSlots + timeSlot)
    }
    /**
     * Replaces an existing time slot with a new one based on the time slot ID.
     * @param slotId The ID of the time slot to be replaced.
     * @param newSlot The new time slot to replace the existing one.
     * @return A new instance of Event with the updated time slots.
     */
    fun replaceSlot(slotId: Int, newSlot: TimeSlot): Event {
        val updatedTimeSlots = timeSlots.map {
            if (it.id == slotId) newSlot else it
        }
        return this.copy(timeSlots = updatedTimeSlots)
    }
}