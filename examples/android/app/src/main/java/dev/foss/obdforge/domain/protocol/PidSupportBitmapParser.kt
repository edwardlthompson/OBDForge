package dev.foss.obdforge.domain.protocol

object PidSupportBitmapParser {
    val bitmapQueryPids: List<Int> = listOf(
        0x00, 0x20, 0x40, 0x60, 0x80, 0xA0, 0xC0, 0xE0,
    )

    fun parseSupportedPids(response: String, bitmapPid: Int): Set<Int> {
        val parsed = ObdIsoResponseParser.parseMode01(response, bitmapPid) ?: return emptySet()
        if (parsed.payload.size < 4) return emptySet()
        val result = mutableSetOf<Int>()
        for (byteIndex in 0 until 4) {
            var mask = 0x80
            val byte = parsed.payload[byteIndex].toInt() and 0xFF
            for (bit in 0 until 8) {
                if (byte and mask != 0) {
                    result.add(bitmapPid + 1 + byteIndex * 8 + bit)
                }
                mask = mask shr 1
            }
        }
        return result
    }
}
