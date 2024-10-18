package pt.isel

interface UpdatedTimeSlotEmitter {
    fun emit(signal: UpdatedTimeSlot)

    fun onCompletion(callback: () -> Unit)

    fun onError(callback: (Throwable) -> Unit)
}
