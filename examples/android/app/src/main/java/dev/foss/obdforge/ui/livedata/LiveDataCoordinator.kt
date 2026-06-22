package dev.foss.obdforge.ui.livedata

import dev.foss.obdforge.data.livedata.LiveDataStreamEngine
import dev.foss.obdforge.data.livedata.PidSupportDiscovery
import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.livedata.LiveDataLayoutResolver
import dev.foss.obdforge.domain.livedata.LiveDataSnapshot
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.transport.ObdTransport
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LiveDataCoordinator(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry,
    private val selection: TransportSelection,
) {
    private val _snapshot = MutableStateFlow(LiveDataSnapshot(emptyMap(), 0))
    val snapshot: StateFlow<LiveDataSnapshot> = _snapshot.asStateFlow()

    private val _running = MutableStateFlow(false)
    val running: StateFlow<Boolean> = _running.asStateFlow()

    private val _paused = MutableStateFlow(false)
    val paused: StateFlow<Boolean> = _paused.asStateFlow()

    private var engine: LiveDataStreamEngine? = null
    private var collectJob: Job? = null
    private var transport: ObdTransport? = null
    private var protocol: DiagnosticProtocol? = null

    suspend fun start(scope: CoroutineScope, persona: PersonaMode): Result<Unit> {
        stop(scope)
        val activeTransport = transportRegistry.create(selection.type, selection.endpoint)
            ?: return Result.failure(IllegalStateException("Transport unavailable"))
        activeTransport.connect().getOrElse { return Result.failure(it) }
        val activeProtocol = protocolRegistry.selectBest(activeTransport)
            ?: return Result.failure(IllegalStateException("No supported protocol"))
        val supportedPids = PidSupportDiscovery().discoverSupportedCatalogPids(
            protocol = activeProtocol,
            transport = activeTransport,
        )
        val layout = LiveDataLayoutResolver.resolve(persona, supportedPids)
        val streamEngine = LiveDataStreamEngine(
            protocol = activeProtocol,
            transport = activeTransport,
            pids = layout.pids,
            pollIntervalMs = layout.pollIntervalMs,
        )
        transport = activeTransport
        protocol = activeProtocol
        engine = streamEngine
        collectJob = scope.launch {
            streamEngine.snapshot.collect { _snapshot.value = it }
        }
        streamEngine.start(scope)
        _running.value = true
        _paused.value = false
        return Result.success(Unit)
    }

    fun pause() {
        engine?.pause()
        _paused.value = true
    }

    fun resume() {
        engine?.resume()
        _paused.value = false
    }

    fun stop(scope: CoroutineScope) {
        engine?.stop()
        engine = null
        collectJob?.cancel()
        collectJob = null
        transport?.let { active ->
            scope.launch { active.disconnect() }
        }
        transport = null
        protocol = null
        _running.value = false
        _paused.value = false
        _snapshot.value = LiveDataSnapshot(emptyMap(), 0)
    }
}
