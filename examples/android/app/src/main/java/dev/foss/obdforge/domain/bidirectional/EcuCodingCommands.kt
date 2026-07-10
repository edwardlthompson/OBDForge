package dev.foss.obdforge.domain.bidirectional

/**
 * Validates UDS DID coding payloads. Rejects flash-shaped transfer services.
 */
object EcuCodingCommands {
    const val REQUEST_TIMEOUT_MS = 5_000L
    const val MAX_DID_DATA_BYTES = 64

    private val FLASH_SERVICES = setOf(0x34, 0x36, 0x37)

    data class CodingRequest(
        val service: Int,
        val did: Int,
        val data: ByteArray,
        val wireCommand: String,
    )

    fun buildRead(didHex: String): Result<CodingRequest> {
        val did = parseDid(didHex).getOrElse { return Result.failure(it) }
        val wire = "22" + did.toString(16).uppercase().padStart(4, '0')
        return Result.success(CodingRequest(service = 0x22, did = did, data = ByteArray(0), wireCommand = wire))
    }

    fun buildWrite(didHex: String, dataHex: String): Result<CodingRequest> {
        val did = parseDid(didHex).getOrElse { return Result.failure(it) }
        val data = parseHexBytes(dataHex).getOrElse { return Result.failure(it) }
        if (data.isEmpty()) {
            return Result.failure(IllegalArgumentException("DID write data is empty"))
        }
        if (data.size > MAX_DID_DATA_BYTES) {
            return Result.failure(IllegalArgumentException("DID payload exceeds $MAX_DID_DATA_BYTES bytes"))
        }
        val wire = "2E" + did.toString(16).uppercase().padStart(4, '0') +
            data.joinToString("") { b -> b.toUByte().toString(16).uppercase().padStart(2, '0') }
        return Result.success(CodingRequest(service = 0x2E, did = did, data = data, wireCommand = wire))
    }

    fun rejectIfFlashShaped(command: String): Result<Unit> {
        val hex = command.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        if (hex.length < 2) return Result.success(Unit)
        val service = hex.substring(0, 2).toIntOrNull(16) ?: return Result.success(Unit)
        if (service in FLASH_SERVICES) {
            return Result.failure(
                IllegalArgumentException("Flash/transfer services (34/36/37) are not supported"),
            )
        }
        if (service == 0x27) {
            return Result.failure(
                IllegalArgumentException("Security access (27) is vehicle-specific and unsupported"),
            )
        }
        return Result.success(Unit)
    }

    fun isNegativeResponse(response: String): Boolean {
        val hex = response.replace(" ", "").replace("\r", "").replace("\n", "").uppercase()
        return hex.contains("7F")
    }

    private fun parseDid(didHex: String): Result<Int> {
        val clean = didHex.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }
        if (clean.length !in 1..4) {
            return Result.failure(IllegalArgumentException("DID must be 1–4 hex digits"))
        }
        val value = clean.toIntOrNull(16)
            ?: return Result.failure(IllegalArgumentException("Invalid DID hex"))
        if (value !in 0x0000..0xFFFF) {
            return Result.failure(IllegalArgumentException("DID out of range"))
        }
        return Result.success(value)
    }

    private fun parseHexBytes(dataHex: String): Result<ByteArray> {
        val clean = dataHex.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }.uppercase()
        if (clean.isEmpty()) return Result.success(ByteArray(0))
        if (clean.length % 2 != 0) {
            return Result.failure(IllegalArgumentException("Data hex must have even length"))
        }
        return Result.success(
            ByteArray(clean.length / 2) { i ->
                clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
            },
        )
    }
}
