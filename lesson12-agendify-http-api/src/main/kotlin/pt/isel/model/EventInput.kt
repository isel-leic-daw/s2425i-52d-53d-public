package pt.isel.model

import pt.isel.Participant
import pt.isel.SelectionType

data class EventInput(
    val title: String,
    val description: String?,
    val organizerId: Int,
    val selectionType: SelectionType
)
