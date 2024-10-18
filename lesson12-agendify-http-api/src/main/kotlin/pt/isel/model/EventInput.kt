package pt.isel.model

import pt.isel.SelectionType

data class EventInput(
    val title: String,
    val description: String?,
    val selectionType: SelectionType,
)
