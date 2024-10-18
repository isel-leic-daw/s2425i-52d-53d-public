package pt.isel

import jakarta.annotation.PreDestroy
import jakarta.inject.Named
import kotlinx.datetime.Clock
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

sealed class EventError {
    data object EventNotFound : EventError()

    data object UserNotFound : EventError()

    data object TimeSlotNotFound : EventError()

    data object SingleTimeSlotAlreadyAllocated : EventError()

    data object UserIsAlreadyParticipantInTimeSlot : EventError()
}

sealed class TimeSlotError {
    data object TimeSlotNotFound : TimeSlotError()

    data object TimeSlotSingleHasNotMultipleParticipants : TimeSlotError()
}

@Named
class EventService(
    private val trxManager: TransactionManager,
) {
    // Important: mutable state on a singleton service
    private val listeners = mutableMapOf<Event, List<UpdatedTimeSlotEmitter>>()
    private var currentId = 0L
    private val lock = ReentrantLock()

    // A scheduler to send the periodic keep-alive events
    private val scheduler: ScheduledExecutorService =
        Executors.newScheduledThreadPool(1).also {
            it.scheduleAtFixedRate({ keepAlive() }, 2, 2, TimeUnit.SECONDS)
        }

    @PreDestroy
    fun shutdown() {
        logger.info("shutting down")
        scheduler.shutdown()
    }

    fun addEmitter(
        eventId: Int,
        listener: UpdatedTimeSlotEmitter,
    ) = lock.withLock {
        val ev =
            trxManager.run {
                repoEvents.findById(eventId)
            }
        requireNotNull(ev)

        logger.info("adding listener")
        val oldListeners = listeners.getOrDefault(ev, emptyList())
        listeners.putIfAbsent(ev, oldListeners + listener)
        listener.onCompletion {
            logger.info("onCompletion")
            removeEmitter(ev, listener)
        }
        listener.onError {
            logger.info("onError")
            removeEmitter(ev, listener)
        }
        listener
    }

    private fun removeEmitter(
        ev: Event,
        listener: UpdatedTimeSlotEmitter,
    ) = lock.withLock {
        logger.info("removing listener")
        val oldListeners = listeners[ev]
        requireNotNull(oldListeners)
        listeners.putIfAbsent(ev, oldListeners - listener)
    }

    private fun keepAlive() =
        lock.withLock {
            logger.info("keepAlive, sending to {} listeners", listeners.values.flatten().size)
            val signal = UpdatedTimeSlot.KeepAlive(Clock.System.now())
            listeners.values.flatten().forEach {
                try {
                    it.emit(signal)
                } catch (ex: Exception) {
                    logger.info("Exception while sending keepAlive signal - {}", ex.message)
                }
            }
        }

    private fun sendEventToAll(
        ev: Event,
        signal: UpdatedTimeSlot,
    ) {
        listeners[ev]?.forEach {
            try {
                it.emit(signal)
            } catch (ex: Exception) {
                logger.info("Exception while sending Message signal - {}", ex.message)
            }
        }
    }

    /**
     * Add participant to a time slot
     */
    fun addParticipantToTimeSlot(
        timeSlotId: Int,
        userId: Int,
    ): Either<EventError, TimeSlot> =
        trxManager.run {
            // Find the time slot within the event
            val timeSlot: TimeSlot =
                repoSlots.findById(timeSlotId)
                    ?: return@run failure(EventError.TimeSlotNotFound)

            // Fetch the User
            val user =
                repoUsers.findById(userId)
                    ?: return@run failure(EventError.UserNotFound)

            when (timeSlot) {
                is TimeSlotSingle -> {
                    // A TimeSlotSingle with already an owner cannot be allocated to other participant
                    if (timeSlot.owner != null) {
                        return@run failure(EventError.SingleTimeSlotAlreadyAllocated)
                    }
                    // Try to add the participant to the time slot
                    val updatedTimeSlot = timeSlot.addOwner(user)

                    // Replace the old time slot with the updated one in the event
                    repoSlots.save(updatedTimeSlot)
                    sendEventToAll(timeSlot.event, UpdatedTimeSlot.Message(++currentId, updatedTimeSlot))

                    // Return the updated event in the Either type
                    success(updatedTimeSlot)
                }

                is TimeSlotMultiple -> {
                    // Return Failure if the user is already a participant in that TimeSlot
                    if (repoParticipants.findByEmail(user.email, timeSlot) != null) {
                        return@run failure(EventError.UserIsAlreadyParticipantInTimeSlot)
                    }
                    // Otherwise, create a new Participant in that TimeSlot for that user.
                    repoParticipants.createParticipant(user, timeSlot)
                    sendEventToAll(timeSlot.event, UpdatedTimeSlot.Message(++currentId, timeSlot))
                    success(timeSlot)
                }
            }
        }

    /**
     * Create a free time slot based on event's selection type
     */
    fun createFreeTimeSlot(
        eventId: Int,
        startTime: LocalDateTime,
        durationInMinutes: Int,
    ): Either<EventError.EventNotFound, TimeSlot> =
        trxManager.run {
            val event = repoEvents.findById(eventId) ?: return@run failure(EventError.EventNotFound)

            // Determine TimeSlot type based on Event's selection type and create it on Repository
            val timeSlot =
                when (event.selectionType) {
                    SelectionType.SINGLE -> repoSlots.createTimeSlotSingle(startTime, durationInMinutes, event)
                    SelectionType.MULTIPLE -> repoSlots.createTimeSlotMultiple(startTime, durationInMinutes, event)
                }
            return@run success(timeSlot)
        }

    fun getAllEvents(): List<Event> = trxManager.run { repoEvents.findAll() }

    fun getEventById(eventId: Int): Either<EventError.EventNotFound, Event> =
        trxManager.run {
            repoEvents
                .findById(eventId)
                ?.let { success(it) }
                ?: failure(EventError.EventNotFound)
        }

    fun createEvent(
        title: String,
        description: String?,
        organizerId: Int,
        selectionType: SelectionType,
    ): Either<EventError.UserNotFound, Event> =
        trxManager.run {
            val organizer = repoUsers.findById(organizerId) ?: return@run failure(EventError.UserNotFound)
            success(repoEvents.createEvent(title, description, organizer, selectionType))
        }

    fun getParticipantsInTimeSlot(timeSlotId: Int): Either<TimeSlotError, List<Participant>> =
        trxManager.run {
            val slot = repoSlots.findById(timeSlotId) ?: return@run failure(TimeSlotError.TimeSlotNotFound)
            when (slot) {
                is TimeSlotSingle -> failure(TimeSlotError.TimeSlotSingleHasNotMultipleParticipants)
                is TimeSlotMultiple -> success(repoParticipants.findAllByTimeSlot(slot))
            }
        }

    companion object {
        private val logger = LoggerFactory.getLogger(EventService::class.java)
    }
}
