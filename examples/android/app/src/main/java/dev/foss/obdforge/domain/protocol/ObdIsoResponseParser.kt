package dev.foss.obdforge.domain.protocol

object ObdIsoResponseParser {
    private val ELM_ID_PATTERN = Regex("""ELM\d{3}""", RegexOption.IGNORE_CASE)

    fun looksLikeElm327(identifyResponse: String): Boolean =
        ELM_ID_PATTERN.containsMatchIn(identifyResponse) ||
            identifyResponse.contains("OBDForge", ignoreCase = true)

    fun parseMode01(response: String, pid: Int): PidResponse? {
        val hex = normalizeHex(response)
        val prefix = "41" + pid.toString(16).uppercase().padStart(2, '0')
        val index = hex.indexOf(prefix)
        if (index < 0) return null
        val dataHex = hex.substring(index + 4)
        if (dataHex.length < 2) return null
        val payload = hexToBytes(dataHex)
        return PidResponse(mode = ObdMode.Mode01, pid = pid, payload = payload, raw = response)
    }

    /** Mode 02 freeze-frame: response prefix 42 + PID (same payload layout as Mode 01). */
    fun parseMode02(response: String, pid: Int): PidResponse? {
        val hex = normalizeHex(response)
        val prefix = "42" + pid.toString(16).uppercase().padStart(2, '0')
        val index = hex.indexOf(prefix)
        if (index < 0) return null
        val dataHex = hex.substring(index + 4)
        if (dataHex.length < 2) return null
        val payload = hexToBytes(dataHex)
        return PidResponse(mode = ObdMode.Mode02, pid = pid, payload = payload, raw = response)
    }

    fun parseMode03(response: String): DtcList? = parseDtcMode(response, "43")

    /** Mode 07 pending DTCs — same DTC encoding as Mode 03, response prefix 47. */
    fun parseMode07(response: String): DtcList? = parseDtcMode(response, "47")

    private fun parseDtcMode(response: String, modePrefix: String): DtcList? {
        val hex = normalizeHex(response)
        val index = hex.indexOf(modePrefix)
        if (index < 0) return null
        val dataHex = hex.substring(index + 2)
        if (dataHex.isEmpty()) return DtcList(emptyList(), response)
        val entries = mutableListOf<DtcEntry>()
        var offset = 0
        while (offset + 3 < dataHex.length) {
            val b1 = dataHex.substring(offset, offset + 2).toInt(16)
            val b2 = dataHex.substring(offset + 2, offset + 4).toInt(16)
            if (b1 == 0 && b2 == 0) break
            val rawPair = "${dataHex.substring(offset, offset + 2)} ${dataHex.substring(offset + 2, offset + 4)}"
            entries.add(DtcEntry(code = decodeDtc(b1, b2), raw = rawPair))
            offset += 4
        }
        return DtcList(entries = entries, raw = response)
    }

    fun parseMode04(response: String): Boolean =
        normalizeHex(response).contains("44")

    fun parseMode09Vin(response: String): String? {
        val hex = normalizeHex(response)
        val dataStart = hex.indexOf("4902")
        if (dataStart < 0) return null
        val payload = hex.substring(dataStart + 6)
        if (payload.length < 34) return null
        return buildString {
            for (i in 0 until 17) {
                append(payload.substring(i * 2, i * 2 + 2).toInt(16).toChar())
            }
        }.filter { it.isLetterOrDigit() }.takeIf { it.length == 17 }
    }

    fun decodeDtc(byte1: Int, byte2: Int): String {
        val type = when ((byte1 and 0xC0) shr 6) {
            0 -> 'P'
            1 -> 'C'
            2 -> 'B'
            else -> 'U'
        }
        val d1 = (byte1 shr 4) and 0x03
        val d2 = byte1 and 0x0F
        val d3 = (byte2 shr 4) and 0x0F
        val d4 = byte2 and 0x0F
        return "$type$d1${d2.toString(16)}${d3.toString(16)}${d4.toString(16)}".uppercase()
    }

    internal fun normalizeHexForTest(response: String): String = normalizeHex(response)

    private fun normalizeHex(response: String): String =
        response.replace(" ", "").replace("\r", "").replace("\n", "").uppercase()

    private fun hexToBytes(hex: String): ByteArray {
        val clean = hex.filter { it.isDigit() || it in 'A'..'F' }
        val size = clean.length / 2
        return ByteArray(size) { index ->
            clean.substring(index * 2, index * 2 + 2).toInt(16).toByte()
        }
    }
}
