package dev.foss.obdforge.data.demo

object DemoObdFixtures {
    const val ADAPTER_RESET: String = "ELM327 v2.3"
    const val ADAPTER_ID: String = "ELM327 v2.3 (OBDForge Demo)"
    const val DEMO_VIN: String = "1G1JC5442R7251234"
    const val PRIMARY_DTC: String = "P0133"

    val mode01Responses: Map<Int, String> = mapOf(
        0x00 to "41 00 BE 3F A8 13",
        0x03 to "41 03 02 02",
        0x04 to "41 04 50",
        0x05 to "41 05 92",
        0x06 to "41 06 80",
        0x07 to "41 07 7E",
        0x0B to "41 0B 64",
        0x0C to "41 0C 0F A0",
        0x0D to "41 0D 32",
        0x0F to "41 0F 55",
        0x10 to "41 10 01 F4",
        0x11 to "41 11 80",
        0x14 to "41 14 64 FF",
        0x1F to "41 1F 01 2C",
        0x20 to "41 20 80 00 00 01",
        0x24 to "41 24 80 00 00 00",
        0x2F to "41 2F 78",
        0x40 to "41 40 48 00 00 00",
        0x42 to "41 42 0E 74",
        0x44 to "41 44 80 00",
        0x46 to "41 46 62",
        0x5C to "41 5C 7A",
    )

    fun responseFor(command: String): String {
        val normalized = command.trim().uppercase()
        return when {
            normalized == "ATZ" -> ADAPTER_RESET
            normalized == "ATI" -> ADAPTER_ID
            normalized == "ATE0" || normalized == "ATL0" || normalized == "ATSP0" -> "OK"
            normalized == "STBC 1" || normalized == "STBC 0" || normalized == "STBCOF 1" -> "OK"
            normalized.contains("0902") || normalized == "0902" -> mode09VinResponse()
            normalized.replace(" ", "") == "22F190" -> udsF190VinResponse()
            normalized.replace(" ", "").startsWith("22") -> udsReadDidResponse(normalized)
            normalized.replace(" ", "").startsWith("2E") -> udsWriteDidResponse(normalized)
            normalized.replace(" ", "") == "1A90" -> kwpVinResponse()
            normalized == "J1939VIN" -> DemoObdFixtures.DEMO_VIN
            normalized == "03" -> "43 01 33 01 71 00 00 00"
            normalized == "07" -> "47 01 33 00 00 00 00 00"
            normalized == "04" -> "44"
            normalized.replace(" ", "").startsWith("2F") -> "6F 01 00"
            normalized.replace(" ", "").startsWith("08") -> "48 01 00"
            normalized.startsWith("02") -> mode02CommandResponse(normalized)
            normalized.contains("|") ->
                normalized.split("|").joinToString(" | ") { mode01CommandResponse(it) }
            normalized.startsWith("STPX D:") -> mode01CommandResponse(normalized.removePrefix("STPX D:"))
            else -> mode01CommandResponse(normalized)
        }
    }

    private fun mode02CommandResponse(command: String): String {
        val pidHex = command.filter { it.isDigit() || it in 'A'..'F' }
            .takeLast(2)
            .toIntOrNull(16)
            ?: return "OK"
        val mode01 = mode01Responses[pidHex] ?: return "OK"
        return mode01.replaceFirst("41", "42")
    }

    private fun udsReadDidResponse(command: String): String {
        val hex = command.replace(" ", "")
        if (hex.length < 6) return "7F 22 13"
        val did = hex.substring(2, 6)
        return "62 $did AA BB"
    }

    private fun udsWriteDidResponse(command: String): String {
        val hex = command.replace(" ", "")
        if (hex.length < 6) return "7F 2E 13"
        val did = hex.substring(2, 6)
        return "6E $did"
    }

    private fun mode09VinResponse(): String =
        "49 02 01 31 47 31 4A 43 35 34 34 32 52 37 32 35 31 32 33 34"

    private fun udsF190VinResponse(): String =
        "62 F1 90 31 47 31 4A 43 35 34 34 32 52 37 32 35 31 32 33 34"

    private fun kwpVinResponse(): String =
        "5A 90 31 47 31 4A 43 35 34 34 32 52 37 32 35 31 32 33 34"

    private fun mode01CommandResponse(command: String): String {
        val pidHex = command.filter { it.isDigit() || it in 'A'..'F' }
            .takeLast(2)
            .toIntOrNull(16)
            ?: return "OK"
        return mode01Responses[pidHex] ?: "OK"
    }
}
