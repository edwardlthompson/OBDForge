package dev.foss.obdforge.domain.vehicle

import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.transport.ObdTransport

enum class VinSourceType {
    EcuObd2,
    EcuUds,
    EcuKwp,
    EcuJ1939,
    Manual,
    Barcode,
    PlateLookup,
    Demo,
}

data class VinReadResult(
    val vin: String,
    val source: VinSourceType,
    val confidence: Float,
)

object VinResolver {
    private const val DEMO_VIN = "1G1JC5444R7251234"

    suspend fun readFromEcu(transport: ObdTransport): VinReadResult? {
        val mode09 = transport.send("0902").getOrNull() ?: return null
        val vin = parseMode09Vin(mode09) ?: return null
        if (vin.length != 17) return null
        return VinReadResult(vin = vin, source = VinSourceType.EcuObd2, confidence = 0.95f)
    }

    fun demoVin(): VinReadResult =
        VinReadResult(vin = DEMO_VIN, source = VinSourceType.Demo, confidence = 1.0f)

    internal fun parseMode09Vin(response: String): String? =
        ObdIsoResponseParser.parseMode09Vin(response)
}
