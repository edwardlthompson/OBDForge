package dev.foss.obdforge.domain.vehicle

import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.transport.ObdTransport

internal object EcuVinReaders {
    suspend fun readMode09(transport: ObdTransport): VinReadResult? {
        val response = transport.send("0902").getOrNull() ?: return null
        val vin = ObdIsoResponseParser.parseMode09Vin(response) ?: return null
        return toResult(vin, VinSourceType.EcuObd2, 0.95f)
    }

    suspend fun readUdsF190(transport: ObdTransport): VinReadResult? {
        val response = transport.send("22 F1 90").getOrNull() ?: return null
        val vin = parseAsciiHexVin(response, prefix = "62F190") ?: return null
        return toResult(vin, VinSourceType.EcuUds, 0.92f)
    }

    suspend fun readKwp(transport: ObdTransport): VinReadResult? {
        val response = transport.send("1A 90").getOrNull() ?: return null
        val vin = parseAsciiHexVin(response, prefix = "5A90") ?: return null
        return toResult(vin, VinSourceType.EcuKwp, 0.88f)
    }

    suspend fun readJ1939(transport: ObdTransport): VinReadResult? {
        val response = transport.send("J1939VIN").getOrNull() ?: return null
        val vin = response.filter { it.isLetterOrDigit() }.uppercase().takeIf { it.length == 17 } ?: return null
        return toResult(vin, VinSourceType.EcuJ1939, 0.85f)
    }

    private fun toResult(raw: String, source: VinSourceType, confidence: Float): VinReadResult? {
        val normalized = VinValidator.normalize(raw)
        if (normalized.length != 17) return null
        return VinReadResult(vin = normalized, source = source, confidence = confidence)
    }

    private fun parseAsciiHexVin(response: String, prefix: String): String? {
        val hex = response.replace(" ", "").replace("\r", "").replace("\n", "").uppercase()
        val index = hex.indexOf(prefix)
        if (index < 0) return null
        val payload = hex.substring(index + prefix.length)
        if (payload.length < 34) return null
        return buildString {
            for (i in 0 until 17) {
                append(payload.substring(i * 2, i * 2 + 2).toInt(16).toChar())
            }
        }.filter { it.isLetterOrDigit() }.takeIf { it.length == 17 }
    }
}
