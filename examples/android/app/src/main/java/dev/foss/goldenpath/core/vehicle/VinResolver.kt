package dev.foss.goldenpath.core.vehicle

import dev.foss.goldenpath.core.transport.Transport

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

    suspend fun readFromEcu(transport: Transport): VinReadResult? {
        val mode09 = transport.send("0902").getOrNull() ?: return null
        val vin = parseMode09Vin(mode09) ?: return null
        if (vin.length != 17) return null
        return VinReadResult(vin = vin, source = VinSourceType.EcuObd2, confidence = 0.95f)
    }

    fun demoVin(): VinReadResult =
        VinReadResult(vin = DEMO_VIN, source = VinSourceType.Demo, confidence = 1.0f)

    internal fun parseMode09Vin(response: String): String? {
        val hex = response.replace(" ", "").uppercase()
        val dataStart = hex.indexOf("4902")
        if (dataStart < 0) return null
        val payload = hex.substring(dataStart + 6)
        if (payload.length < 34) return null
        return buildString {
            for (i in 0 until 17) {
                val byteHex = payload.substring(i * 2, i * 2 + 2)
                append(byteHex.toInt(16).toChar())
            }
        }.filter { it.isLetterOrDigit() }.takeIf { it.length == 17 }
    }
}
