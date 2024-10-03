package pt.isel.model

import pt.isel.ParticipantKind

data class ParticipantInput(val name: String, val email: String, val kind: ParticipantKind)
