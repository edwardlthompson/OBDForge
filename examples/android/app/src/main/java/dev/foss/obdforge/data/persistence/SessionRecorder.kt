package dev.foss.obdforge.data.persistence

import dev.foss.obdforge.data.preferences.TransportSelection
import dev.foss.obdforge.data.registry.ProtocolRegistry
import dev.foss.obdforge.data.registry.TransportRegistry
import dev.foss.obdforge.domain.livedata.PidCatalog
import dev.foss.obdforge.domain.livedata.PersonaMode
import dev.foss.obdforge.domain.livedata.PidValueParser
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.ObdMode as ProtocolObdMode
import dev.foss.obdforge.domain.transport.ObdTransport
import dev.foss.obdforge.domain.vehicle.VinReadResult
import dev.foss.obdforge.feature.livedata.logic.PidFormatter

class SessionRecorder(
    private val transportRegistry: TransportRegistry,
    private val protocolRegistry: ProtocolRegistry,
    private val sessionRepository: SessionRepository,
) {
    suspend fun recordFromConnection(
        selection: TransportSelection,
        vinResult: VinReadResult,
    ): Long? {
        val transport = transportRegistry.create(selection.type, selection.endpoint) ?: return null
        if (transport.state != dev.foss.obdforge.domain.transport.ConnectionState.Connected) {
            transport.connect().getOrElse { return null }
        }
        val protocol = protocolRegistry.selectBest(transport) ?: return null
        return recordSession(transport, protocol, selection, vinResult)
    }

    private suspend fun recordSession(
        transport: ObdTransport,
        protocol: DiagnosticProtocol,
        selection: TransportSelection,
        vinResult: VinReadResult,
    ): Long {
        val sessionId = sessionRepository.startSession(
            transportType = selection.type.name,
            protocolId = protocol.id.wireName,
            vin = vinResult.vin,
        )
        protocol.readDtcs(transport).getOrNull()?.let { dtcList ->
            sessionRepository.saveDtcSnapshot(sessionId, dtcList)
            dtcList.entries.forEach { entry ->
                sessionRepository.saveFreezeFrame(
                    sessionId = sessionId,
                    dtcCode = entry.code,
                    pidValues = sampleFreezeFrame(protocol, transport),
                )
            }
        }
        sessionRepository.endSession(sessionId)
        return sessionId
    }

    private suspend fun sampleFreezeFrame(
        protocol: DiagnosticProtocol,
        transport: ObdTransport,
    ): Map<String, String> = buildMap {
        for (definition in PidCatalog.forPersona(PersonaMode.Diy)) {
            val response = protocol.readPid(transport, ProtocolObdMode.Mode01, definition.pid).getOrNull()
                ?: continue
            val parsed = PidValueParser.parse(response) ?: continue
            put("%02X".format(definition.pid), PidFormatter.displayLabel(parsed))
        }
    }
}
