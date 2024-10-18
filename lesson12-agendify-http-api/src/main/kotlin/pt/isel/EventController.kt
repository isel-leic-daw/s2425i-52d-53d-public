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

    /**
     * Try with:
     curl -X POST http://localhost:8080/api/events \
     -H "Authorization: Bearer n8AVqGnzbGkvDPryo8t14kWz6KfQIxygL3AH-HHMS28=" \
     -H "Content-Type: application/json" \
     -d '{
     "title": "Arrakis Sandstorm Meeting",
     "description": "Discuss plans for the Fremen alliance",
     "selectionType": "SINGLE"
     }'
     */
    @PostMapping
    fun createEvent(
        organizer: AuthenticatedUser,
        @RequestBody ev: EventInput,
    ): ResponseEntity<Any> {
        val event: Either<EventError.UserNotFound, Event> =
            eventService.createEvent(ev.title, ev.description, organizer.user.id, ev.selectionType)
        return when (event) {
            is Success -> ResponseEntity.ok(event.value.id)
            is Failure -> Problem.ParticipantNotFound.response(HttpStatus.NOT_FOUND)
        }
    }
}
