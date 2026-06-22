package dev.foss.obdforge.domain.vehicle

import dev.foss.obdforge.domain.protocol.ObdIsoResponseParser
import dev.foss.obdforge.domain.transport.ConnectionState
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
    const val DEMO_VIN = "1G1JC5442R7251234"

    suspend fun resolveFromEcu(transport: ObdTransport): VinReadResult? {
        if (!ensureConnected(transport)) return null
        EcuVinReaders.readMode09(transport)?.let { return it }
        EcuVinReaders.readUdsF190(transport)?.let { return it }
        EcuVinReaders.readKwp(transport)?.let { return it }
        EcuVinReaders.readJ1939(transport)?.let { return it }
        return null
    }

    suspend fun readFromEcu(transport: ObdTransport): VinReadResult? = resolveFromEcu(transport)

    fun fromManual(vin: String): VinReadResult? =
        when (val result = VinValidator.validate(vin)) {
            is VinValidationResult.Valid -> VinReadResult(result.vin, VinSourceType.Manual, 0.75f)
            is VinValidationResult.Invalid -> null
        }

    fun fromBarcode(vin: String): VinReadResult? =
        when (val result = VinValidator.validate(vin)) {
            is VinValidationResult.Valid -> VinReadResult(result.vin, VinSourceType.Barcode, 0.90f)
            is VinValidationResult.Invalid -> null
        }

    fun demoVin(): VinReadResult =
        VinReadResult(vin = DEMO_VIN, source = VinSourceType.Demo, confidence = 1.0f)

    internal fun parseMode09Vin(response: String): String? =
        ObdIsoResponseParser.parseMode09Vin(response)

    private suspend fun ensureConnected(transport: ObdTransport): Boolean {
        if (transport.state == ConnectionState.Connected) return true
        return transport.connect().isSuccess
    }
}
