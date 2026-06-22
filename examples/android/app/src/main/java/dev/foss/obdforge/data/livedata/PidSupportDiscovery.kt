package dev.foss.obdforge.data.livedata

import dev.foss.obdforge.domain.livedata.PidCatalog
import dev.foss.obdforge.domain.protocol.DiagnosticProtocol
import dev.foss.obdforge.domain.protocol.ObdMode
import dev.foss.obdforge.domain.protocol.PidSupportBitmapParser
import dev.foss.obdforge.domain.transport.ObdTransport

class PidSupportDiscovery {
    suspend fun discoverSupportedCatalogPids(
        protocol: DiagnosticProtocol,
        transport: ObdTransport,
        catalogPids: Collection<Int> = PidCatalog.all.map { it.pid },
    ): Set<Int> {
        val supported = mutableSetOf<Int>()
        for (bitmapPid in PidSupportBitmapParser.bitmapQueryPids) {
            val response = protocol.readPid(transport, ObdMode.Mode01, bitmapPid).getOrNull() ?: continue
            supported.addAll(PidSupportBitmapParser.parseSupportedPids(response.raw, bitmapPid))
        }
        if (supported.isEmpty()) return catalogPids.toSet()
        return supported.intersect(catalogPids.toSet())
    }
}
