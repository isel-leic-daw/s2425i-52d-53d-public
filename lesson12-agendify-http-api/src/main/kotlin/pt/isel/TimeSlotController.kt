package pt.isel

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.EventError.*
import pt.isel.model.Problem
import pt.isel.model.TimeSlotInput

@RestController
@RequestMapping("/api/events")
class TimeSlotController(
    private val eventService: EventService
) {

    @PostMapping("/{eventId}/timeslots")
    fun createFreeTimeSlot(@PathVariable eventId: Int, @RequestBody timeSlotInput: TimeSlotInput): ResponseEntity<Any> {
        val timeSlot: Either<EventNotFound, TimeSlot> =
            eventService.createFreeTimeSlot(
                eventId,
                timeSlotInput.startTime,
                timeSlotInput.durationInMinutes
            )

        return when (timeSlot) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(timeSlot.value)
            is Failure -> ResponseEntity.notFound().build()
        }
    }

    @PostMapping("/{eventId}/timeslots/{timeSlotId}/participants/{participantId}")
    fun addParticipantToTimeSlot(
        @PathVariable eventId: Int,
        @PathVariable timeSlotId: Int,
        @PathVariable participantId: Int
    ): ResponseEntity<Any> {
        val timeSlot: Either<EventError, TimeSlot> =
            eventService.addParticipantToTimeSlot(timeSlotId, participantId)

        return when (timeSlot) {
            is Success -> ResponseEntity.ok(timeSlot.value)
            is Failure -> when (timeSlot.value) {
                is TimeSlotNotFound -> Problem.TimeSlotNotFound.response(HttpStatus.NOT_FOUND)
                is UserNotFound -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
                is EventNotFound -> Problem.EventNotFound.response(HttpStatus.NOT_FOUND)
                is SingleTimeSlotAlreadyAllocated -> Problem.TimeSlotAlreadyAllocated.response(HttpStatus.CONFLICT)
                UserIsAlreadyParticipantInTimeSlot -> Problem.UserIsAlreadyParticipantInTimeSlot.response(HttpStatus.CONFLICT)
            }
        }
    }
}
