package pt.isel.model

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import java.net.URI

private const val MEDIA_TYPE = "application/problem+json"
private const val PROBLEM_URI_PATH = "https://github.com/isel-leic-daw/s2425i-52d-53d-public/tree/main/docs/agendify/problems"

sealed class Problem(
    typeUri: URI,
) {
    val type = typeUri.toString()
    val title = typeUri.toString().split("/").last()

    fun response(status: HttpStatus): ResponseEntity<Any> = ResponseEntity.status(status)
        .header("Content-Type", MEDIA_TYPE)
        .body(this)

    data object EmailAlreadyInUse : Problem(URI("$PROBLEM_URI_PATH/email-already-in-use"))
    data object ParticipantNotFound : Problem(URI("$PROBLEM_URI_PATH/participant-not-found"))
    data object EventNotFound : Problem(URI("$PROBLEM_URI_PATH/event-not-found"))
    data object TimeSlotNotFound : Problem(URI("$PROBLEM_URI_PATH/event-not-found"))
    data object TimeSlotAlreadyAllocated : Problem(URI("$PROBLEM_URI_PATH/timeslot-already-allocated"))

}