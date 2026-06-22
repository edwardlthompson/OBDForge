package dev.foss.obdforge.domain.diagnostics

import dev.foss.obdforge.domain.livedata.ParsedPidValue
import dev.foss.obdforge.domain.livedata.PidUnit
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PidRangeEvaluatorTest {
    @Test
    fun evaluateAll_flagsOverheatingCoolant() {
        val readings = PidRangeEvaluator.evaluateAll(
            listOf(
                ParsedPidValue(0x05, "Coolant temp", PidUnit.Celsius, 110.0),
            ),
        )
        assertEquals(1, readings.size)
        assertEquals(AbnormalReason.AboveMax, readings.first().reason)
    }

    @Test
    fun evaluateAll_flagsHighRpmAtStandstill() {
        val readings = PidRangeEvaluator.evaluateAll(
            listOf(
                ParsedPidValue(0x0C, "Engine RPM", PidUnit.Rpm, 4200.0),
                ParsedPidValue(0x0D, "Vehicle speed", PidUnit.Kph, 0.0),
            ),
        )
        assertTrue(readings.any { it.pid == 0x0C && it.reason == AbnormalReason.AboveMax })
    }

    @Test
    fun evaluateAll_ignoresNormalSamples() {
        val readings = PidRangeEvaluator.evaluateAll(
            listOf(
                ParsedPidValue(0x05, "Coolant temp", PidUnit.Celsius, 85.0),
                ParsedPidValue(0x42, "Control voltage", PidUnit.Volts, 13.8),
            ),
        )
        assertTrue(readings.isEmpty())
    }
}
