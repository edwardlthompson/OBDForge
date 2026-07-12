package dev.foss.obdforge.domain.livedata

/**
 * SAE J1979 Mode 01 PID 0x03 — fuel system status (open vs closed loop).
 * Monitor-only; PCM decides loop state (no OBD write to force open loop).
 */
object FuelSystemStatus {
    const val PID = 0x03

    fun decodePacked(bytes: ByteArray): Double? {
        if (bytes.isEmpty()) return null
        val bank1 = bytes[0].toUByte().toInt() and 0xFF
        val bank2 = if (bytes.size > 1) bytes[1].toUByte().toInt() and 0xFF else 0
        return (bank1 or (bank2 shl 8)).toDouble()
    }

    fun label(statusByte: Int): String = when (statusByte and 0xFF) {
        0 -> "Off"
        1 -> "Open loop (cold)"
        2 -> "Closed loop"
        4 -> "Open loop (load)"
        8 -> "Open loop (fault)"
        16 -> "Closed loop (O2 fault)"
        else -> "Unknown (0x%02X)".format(statusByte and 0xFF)
    }

    fun isClosedLoop(statusByte: Int): Boolean {
        val v = statusByte and 0xFF
        return v == 2 || v == 16
    }

    fun formatPacked(packed: Double): String {
        val raw = packed.toInt()
        val bank1 = raw and 0xFF
        val bank2 = (raw shr 8) and 0xFF
        val primary = label(bank1)
        return if (bank2 == 0) primary else "$primary · B2: ${label(bank2)}"
    }
}
