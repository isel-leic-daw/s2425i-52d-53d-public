package pt.isel

import org.springframework.http.HttpStatus
import pt.isel.model.EventInput
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import pt.isel.model.Problem

@RestController
@RequestMapping("/api/events")
class EventController(
    private val eventService: EventService
) {

    @GetMapping
    fun getAllEvents(): ResponseEntity<List<Event>> {
        val events = eventService.getAllEvents()
        return ResponseEntity.ok(events)
    }

    @GetMapping("/{eventId}")
    fun getEventById(@PathVariable eventId: Int): ResponseEntity<Event> {
        val event: Either<EventError.EventNotFound, Event> = eventService.getEventById(eventId)
        return when (event) {
            is Success -> ResponseEntity.ok(event.value)
            is Failure -> ResponseEntity.notFound().build()
        }
    }

    @PostMapping
    fun createEvent(@RequestBody ev: EventInput): ResponseEntity<Any> {
        val event: Either<EventError.ParticipantNotFound, Event> =
            eventService.createEvent(ev.title, ev.description, ev.organizerId, ev.selectionType)
        return when (event) {
            is Success -> ResponseEntity.ok(event.value)
            is Failure -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
        }
    }
}
