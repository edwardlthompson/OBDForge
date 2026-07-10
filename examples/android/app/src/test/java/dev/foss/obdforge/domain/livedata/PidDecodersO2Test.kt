package dev.foss.obdforge.domain.livedata

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class PidDecodersO2Test {
    @Test
    fun o2Voltage_decodesAndRejectsEmpty() {
        assertEquals(0.5, PidDecoders.o2Voltage()(byteArrayOf(0x64))!!, 0.001)
        assertNull(PidDecoders.o2Voltage()(ByteArray(0)))
    }

    @Test
    fun widebandLambda_decodesAndRejectsShort() {
        assertEquals(1.0, PidDecoders.widebandLambda()(byteArrayOf(0x80.toByte(), 0x00))!!, 0.001)
        assertNull(PidDecoders.widebandLambda()(byteArrayOf(0x80.toByte())))
    }

    @Test
    fun fuelTrimAndMaf() {
        assertEquals(0.0, PidDecoders.fuelTrim()(byteArrayOf(0x80.toByte()))!!, 0.001)
        assertEquals(5.0, PidDecoders.maf()(byteArrayOf(0x01, 0xF4.toByte()))!!, 0.001)
    }
}
