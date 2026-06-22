package dev.foss.obdforge.data.diagnostics

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.diagnostics.PidRangeEvaluator
import dev.foss.obdforge.domain.diagnostics.VehicleHealthSnapshot
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.livedata.PidCatalog
import dev.foss.obdforge.domain.livedata.PidValueParser
import dev.foss.obdforge.domain.protocol.ObdMode

class VehicleHealthScanUseCase(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry,
) {
    suspend fun scan(
        selection: TransportSelection,
        persona: PersonaMode,
    ): Result<VehicleHealthSnapshot> {
        val transport = transportRegistry.create(selection.type, selection.endpoint)
            ?: return Result.failure(IllegalStateException("Transport unavailable"))
        transport.connect().getOrElse { return Result.failure(it) }
        return try {
            val protocol = protocolRegistry.selectBest(transport)
                ?: return Result.failure(IllegalStateException("No supported protocol"))
            val dtcs = protocol.readDtcs(transport).getOrElse { return Result.failure(it) }
            val pids = PidCatalog.forPersona(persona).map { it.pid }
            val responses = pids.mapNotNull { pid ->
                protocol.readPid(transport, ObdMode.Mode01, pid).getOrNull()
            }
            val parsed = PidValueParser.parseAll(responses)
            Result.success(
                VehicleHealthSnapshot(
                    dtcs = dtcs.entries.map { it.code },
                    abnormalPids = PidRangeEvaluator.evaluateAll(parsed),
                ),
            )
        } finally {
            transport.disconnect()
        }
    }
}
