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

    /** Narrowband O2 sensor voltage (A/200). Short/empty → null. */
    fun o2Voltage(): (ByteArray) -> Double? = { bytes ->
        if (bytes.isEmpty()) null else bytes[0].toUByte().toDouble() / 200.0
    }

    /** Wideband equivalence ratio (lambda) from first two bytes: (A*256+B)/32768. */
    fun widebandLambda(): (ByteArray) -> Double? = { bytes ->
        if (bytes.size < 2) null else {
            val raw = (bytes[0].toUByte().toInt() shl 8) + bytes[1].toUByte().toInt()
            raw / 32768.0
        }
    }

    /** Fuel trim: (A-128)*100/128. */
    fun fuelTrim(): (ByteArray) -> Double? = { bytes ->
        if (bytes.isEmpty()) null else (bytes[0].toUByte().toDouble() - 128.0) * 100.0 / 128.0
    }

    /** MAF: (A*256+B)/100 g/s. */
    fun maf(): (ByteArray) -> Double? = u16Scale(100.0)

    /** MAP absolute pressure: A kPa. */
    fun mapKpa(): (ByteArray) -> Double? = { bytes ->
        if (bytes.isEmpty()) null else bytes[0].toUByte().toDouble()
    }
}
