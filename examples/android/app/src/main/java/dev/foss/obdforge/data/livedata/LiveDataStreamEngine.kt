package dev.foss.obdforge.data.livedata

import dev.foss.obdforge.domain.livedata.LiveDataSnapshot
import dev.foss.obdforge.domain.livedata.LivePidSample
import dev.foss.obdforge.domain.livedata.PidValueParser
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.FastStreamingCapable
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.PidResponse
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.feature.livedata.logic.PidFormatter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.concurrent.atomic.AtomicBoolean

class LiveDataStreamEngine(
    private val protocol: DiagnosticProtocol,
    private val transport: ObdTransport,
    private val pids: List<Int>,
    private val pollIntervalMs: Long,
) {
    private val _snapshot = MutableStateFlow(LiveDataSnapshot(emptyMap(), 0))
    val snapshot: StateFlow<LiveDataSnapshot> = _snapshot.asStateFlow()

    private val inFlight = AtomicBoolean(false)
    private var job: Job? = null
    private var paused = false
    private var sequence = 0L

    val isRunning: Boolean
        get() = job?.isActive == true

    fun start(scope: CoroutineScope) {
        if (job?.isActive == true) return
        job = scope.launch {
            while (isActive) {
                if (paused) {
                    delay(RESUME_POLL_MS)
                    continue
                }
                if (!inFlight.compareAndSet(false, true)) {
                    delay(pollIntervalMs)
                    continue
                }
                try {
                    pollOnce()
                } finally {
                    inFlight.set(false)
                }
                delay(pollIntervalMs)
            }
        }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun pause() {
        paused = true
    }

    fun resume() {
        paused = false
    }

    private suspend fun pollOnce() {
        val responses = readPids()
        if (responses.isEmpty()) return
        val now = System.currentTimeMillis()
        val samples = PidValueParser.parseAll(responses).associate { parsed ->
            parsed.pid to LivePidSample(
                pid = parsed.pid,
                name = parsed.name,
                formattedValue = PidFormatter.displayLabel(parsed),
                unit = parsed.unit,
                numericValue = parsed.numericValue,
                updatedAtMs = now,
            )
        }
        sequence += 1
        _snapshot.value = LiveDataSnapshot(samples = samples, sequence = sequence)
    }

    private suspend fun readPids(): List<PidResponse> {
        if (pids.isEmpty()) return emptyList()
        val fast = protocol as? FastStreamingCapable
        if (fast != null && pids.size > 1) {
            val batched = fast.readPidsBatched(transport, ObdMode.Mode01, pids)
            if (batched.isSuccess) return batched.getOrThrow()
        }
        return pids.mapNotNull { pid ->
            protocol.readPid(transport, ObdMode.Mode01, pid).getOrNull()
        }
    }

    companion object {
        private const val RESUME_POLL_MS = 100L
    }
}
