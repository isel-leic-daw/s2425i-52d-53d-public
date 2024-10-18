package pt.isel

import kotlinx.datetime.Instant

sealed interface UpdatedTimeSlot {
    data class Message(
        val id: Long,
        val slot: TimeSlot,
    ) : UpdatedTimeSlot

    data class KeepAlive(
        val timestamp: Instant,
    ) : UpdatedTimeSlot
}
