package pt.isel

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.model.EventInput
import pt.isel.model.Problem

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService,
) {
    @GetMapping
    fun getAllEvents(): ResponseEntity<List<Event>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{eventId}")
    fun getEventById(
        @PathVariable eventId: Int,
    ): ResponseEntity<Event> =
        when (val event = eventService.getEventById(eventId)) {
            is Success -> ResponseEntity.ok(event.value)
            is Failure -> ResponseEntity.notFound().build()
        }

    @PostMapping
    fun createEvent(
        @RequestBody ev: EventInput,
    ): ResponseEntity<Any> {
        val event: Either<EventError.UserNotFound, Event> =
            eventService.createEvent(ev.title, ev.description, ev.organizerId, ev.selectionType)
        return when (event) {
            is Success -> ResponseEntity.ok(event.value)
            is Failure -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
        }
    }
}
