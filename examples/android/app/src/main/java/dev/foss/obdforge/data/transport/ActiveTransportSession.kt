package dev.foss.obdforge.data.transport

import dev.foss.obdforge.domain.transport.ObdTransport
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

/**
 * Ensures a single active OBD transport: disconnect-and-await-close before reconnect.
 */
object ActiveTransportSession {
    private val mutex = Mutex()
    private var current: ObdTransport? = null

    suspend fun replaceWith(next: ObdTransport): ObdTransport = mutex.withLock {
        val previous = current
        current = null
        previous?.disconnect()
        current = next
        next
    }

    suspend fun disconnectCurrent() = mutex.withLock {
        val previous = current
        current = null
        previous?.disconnect()
    }

    fun peek(): ObdTransport? = current
}
