package pt.isel

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import pt.isel.EventError.EventNotFound
import pt.isel.EventError.SingleTimeSlotAlreadyAllocated
import pt.isel.EventError.TimeSlotNotFound
import pt.isel.EventError.UserIsAlreadyParticipantInTimeSlot
import pt.isel.EventError.UserNotFound
import pt.isel.model.Problem
import pt.isel.model.TimeSlotInput
import java.util.concurrent.TimeUnit

@RestController
@RequestMapping("/api/events")
class TimeSlotController(
    private val eventService: EventService,
) {
    /**
     * Try with:
     curl -X POST http://localhost:8080/api/events/1/timeslots \
     -H "Content-Type: application/json" \
     -d '{
     "startTime": "2024-10-10T17:00:00",
     "durationInMinutes": 60
     }'
     */
    @PostMapping("/{eventId}/timeslots")
    fun createFreeTimeSlot(
        @PathVariable eventId: Int,
        @RequestBody timeSlotInput: TimeSlotInput,
    ): ResponseEntity<Any> {
        val timeSlot: Either<EventNotFound, TimeSlot> =
            eventService.createFreeTimeSlot(
                eventId,
                timeSlotInput.startTime,
                timeSlotInput.durationInMinutes,
            )

        return when (timeSlot) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(timeSlot.value)
            is Failure -> ResponseEntity.notFound().build()
        }
    }

    @GetMapping("/{eventId}/listen")
    fun listen(
        @PathVariable eventId: Int,
    ): SseEmitter {
        val sseEmitter = SseEmitter(TimeUnit.HOURS.toMillis(1))
        eventService.addEmitter(
            eventId,
            SseUpdatedTimeSlotEmitterAdapter(
                sseEmitter,
            ),
        )
        return sseEmitter
    }

    /**
     * Try with:
     curl -X PUT http://localhost:8080/api/events/1/timeslots/1/participants \
     -H "Authorization: Bearer n8AVqGnzbGkvDPryo8t14kWz6KfQIxygL3AH-HHMS28=" \
     */
    @PutMapping("/{eventId}/timeslots/{timeSlotId}/participants")
    fun addParticipantToTimeSlot(
        user: AuthenticatedUser,
        @PathVariable eventId: Int,
        @PathVariable timeSlotId: Int,
    ): ResponseEntity<Any> {
        val timeSlot: Either<EventError, TimeSlot> =
            eventService.addParticipantToTimeSlot(timeSlotId, user.user.id)

        return when (timeSlot) {
            is Success -> ResponseEntity.ok(timeSlot.value)
            is Failure ->
                when (timeSlot.value) {
                    is TimeSlotNotFound -> Problem.TimeSlotNotFound.response(HttpStatus.NOT_FOUND)
                    is UserNotFound -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
                    is EventNotFound -> Problem.EventNotFound.response(HttpStatus.NOT_FOUND)
                    is SingleTimeSlotAlreadyAllocated -> Problem.TimeSlotAlreadyAllocated.response(HttpStatus.CONFLICT)
                    UserIsAlreadyParticipantInTimeSlot -> Problem.UserIsAlreadyParticipantInTimeSlot.response(HttpStatus.CONFLICT)
                }
        }
    }
}
