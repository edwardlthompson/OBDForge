package dev.foss.obdforge.feature.livedata.logic

import dev.foss.obdforge.domain.livedata.ParsedPidValue
import dev.foss.obdforge.domain.livedata.PidUnit
import org.junit.Assert.assertEquals
import org.junit.Test

class PidFormatterTest {
    @Test
    fun displayLabel_includesUnit() {
        val value = ParsedPidValue(
            pid = 0x0C,
            name = "Engine RPM",
            unit = PidUnit.Rpm,
            numericValue = 2500.0,
        )
        assertEquals("2500 rpm", PidFormatter.displayLabel(value))
    }

    @Test
    fun format_voltageUsesOneDecimal() {
        val value = ParsedPidValue(
            pid = 0x42,
            name = "Control voltage",
            unit = PidUnit.Volts,
            numericValue = 13.7,
        )
        assertEquals("13.7", PidFormatter.format(value))
    }
}
