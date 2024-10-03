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
)