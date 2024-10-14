package pt.isel.model

import java.time.LocalDateTime

data class TimeSlotInput(
    val startTime: LocalDateTime,
    val durationInMinutes: Int,
)
