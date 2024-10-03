package pt.isel

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import pt.isel.model.ParticipantInput
import pt.isel.model.Problem

@RestController
@RequestMapping("/api/participants")
class ParticipantController(
    private val participantService: ParticipantService
) {
    @PostMapping
    fun createParticipant(@RequestBody participantInput: ParticipantInput): ResponseEntity<*> {
        val result: Either<ParticipantError, Participant> = participantService
            .createParticipant(participantInput.name, participantInput.email, participantInput.kind)

        return when(result) {
            is Success -> ResponseEntity.status(HttpStatus.CREATED).body(result.value)
            is Failure -> when(result.value) {
                is ParticipantError.AlreadyUsedEmailAddress -> Problem.EmailAlreadyInUse.response(
                    HttpStatus.CONFLICT)
            }
        }
    }
}
