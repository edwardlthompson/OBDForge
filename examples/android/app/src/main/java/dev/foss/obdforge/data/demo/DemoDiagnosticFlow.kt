package dev.foss.obdforge.data.demo

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.DtcList
import dev.foss.obdforge.domain.protocol.PidResponse
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.transport.TransportEndpoint
import dev.foss.obdforge.domain.transport.TransportType
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.domain.vehicle.VinResolver

data class DemoFlowResult(
    val vin: VinReadResult,
    val dtcs: DtcList,
    val rpm: PidResponse,
    val protocol: DiagnosticProtocol,
)

object DemoDiagnosticFlow {
    val demoSelection: TransportSelection =
        TransportSelection(TransportType.Simulated, TransportEndpoint.Simulated)

    suspend fun run(
        transportRegistry: TransportRegistry,
        protocolRegistry: ProtocolRegistry,
    ): Result<DemoFlowResult> {
        val transport = transportRegistry.create(demoSelection.type, demoSelection.endpoint)
            ?: return Result.failure(IllegalStateException("Simulated transport unavailable"))
        transport.connect().getOrElse { return Result.failure(it) }
        val protocol = protocolRegistry.selectBest(transport)
            ?: return Result.failure(IllegalStateException("No protocol detected"))
        val vin = VinResolver.readFromEcu(transport) ?: VinResolver.demoVin()
        val dtcs = protocol.readDtcs(transport).getOrElse { return Result.failure(it) }
        val rpm = protocol.readPid(transport, ObdMode.Mode01, 0x0C).getOrElse { return Result.failure(it) }
        transport.disconnect()
        return Result.success(
            DemoFlowResult(
                vin = vin,
                dtcs = dtcs,
                rpm = rpm,
                protocol = protocol,
            ),
        )
    }
}
