package dev.foss.obdforge.domain.livedata

object PidDecoders {
    fun singleByteScale(max: Double): (ByteArray) -> Double? = { bytes ->
        if (bytes.isEmpty()) null else bytes[0].toUByte().toDouble() * max / 255.0
    }

    fun singleByteOffset(offset: Double): (ByteArray) -> Double? = { bytes ->
        if (bytes.isEmpty()) null else bytes[0].toUByte().toDouble() + offset
    }

    fun rpm(): (ByteArray) -> Double? = { bytes ->
        if (bytes.size < 2) null else ((bytes[0].toUByte().toInt() shl 8) + bytes[1].toUByte().toInt()) / 4.0
    }

    fun u16Scale(divisor: Double): (ByteArray) -> Double? = { bytes ->
        if (bytes.size < 2) null else {
            val raw = (bytes[0].toUByte().toInt() shl 8) + bytes[1].toUByte().toInt()
            raw / divisor
        }
    }
}
