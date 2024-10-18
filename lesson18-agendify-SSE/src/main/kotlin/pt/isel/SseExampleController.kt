package pt.isel

import jakarta.annotation.PreDestroy
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import java.util.concurrent.locks.ReentrantLock
import kotlin.concurrent.withLock

@RestController
class SseExampleController {
    // Important: mutable state on a singleton service
    private val listeners = mutableListOf<() -> Unit>()
    private val lock = ReentrantLock()
    private var connectionCounter = 0

    // Single-thread executors to manage the emitters
    private val executor =
        Executors
            .newScheduledThreadPool(1)
            .also {
                it.scheduleAtFixedRate({ keepAlive() }, 2, 2, TimeUnit.SECONDS)
            }

    private fun keepAlive() =
        lock.withLock {
            logger.info("keepAlive, sending to {} listeners", listeners.count())
            if (listeners.isNotEmpty()) {
                sendEventToAll()
            }
        }

    private fun sendEventToAll() {
        listeners.forEach {
            try {
                it()
            } catch (ex: Exception) {
                logger.info("Exception while sending event - {}", ex.message)
            }
        }
    }

    private fun removeListener(listener: () -> Unit) =
        lock.withLock {
            logger.info("removing listener")
            listeners.remove(listener)
        }

    fun addListener(listener: () -> Unit) =
        lock.withLock {
            logger.info("adding listener")
            listeners.add(listener)
        }

    /**
     * Supports reconnect.
     * Try with:
     *   curl -N -H "Last-Event-ID: 1004" http://localhost:8080/api/sse/listen
     */
    @GetMapping("/api/sse/listen")
    fun handleEventStream(
        @RequestHeader(value = "Last-Event-ID", required = false) lastEventId: String?,
    ): SseEmitter {
        val connId = ++connectionCounter
        val emitter = SseEmitter(TimeUnit.HOURS.toMillis(1)) // Timeout is set to never end automatically

        logger.info("Client connected to event stream (connection #$connId, Last-Event-Id: $lastEventId)")
        val lastEventCounter =
            if (lastEventId != null) {
                lastEventId.toInt() % 1000 + 1
            } else {
                1
            }
        // Schedule event emission every second
        val listener: () -> Unit =
            makeListener(connId, emitter, lastEventCounter).also {
                addListener(it)
            }

        // Handle client disconnection
        emitter.onCompletion {
            logger.info("Client disconnected (connection #$connId)")
            removeListener(listener)
        }

        emitter.onTimeout {
            logger.info("Client connection timed out (connection #$connId)")
            removeListener(listener)
        }

        return emitter
    }

    private fun makeListener(
        connId: Int,
        emitter: SseEmitter,
        lastEventCount: Int = 1,
    ): () -> Unit {
        var eventCounter = lastEventCount
        return {
            try {
                // !!! Not reliable and may overlap!!!
                val sseEventId = connId * 1000 + eventCounter
                val eventData = "Server says hi! (event #$eventCounter of connection #$connId)"

                // Send custom event
                emitter.send(
                    SseEmitter
                        .event()
                        .name("my-custom-event")
                        .id(sseEventId.toString())
                        .data(eventData),
                )
                eventCounter++
            } catch (ex: Exception) {
                emitter.completeWithError(ex)
            }
        }
    }

    // Shutdown the executor service when the application is stopped
    @PreDestroy
    fun destroy() {
        logger.info("Stopping scheduler!!!!")
        executor.shutdown()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(SseExampleController::class.java)
    }
}
