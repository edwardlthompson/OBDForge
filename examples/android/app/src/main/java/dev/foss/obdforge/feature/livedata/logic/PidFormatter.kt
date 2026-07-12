package dev.foss.obdforge.feature.livedata.logic

import dev.foss.obdforge.domain.livedata.FuelSystemStatus
import dev.foss.obdforge.domain.livedata.ParsedPidValue
import dev.foss.obdforge.domain.livedata.PidUnit
import kotlin.math.roundToInt

object PidFormatter {
    fun format(value: ParsedPidValue): String {
        val numeric = value.numericValue ?: return "—"
        if (value.pid == FuelSystemStatus.PID) {
            return FuelSystemStatus.formatPacked(numeric)
        }
        return when (value.unit) {
            PidUnit.Rpm -> numeric.roundToInt().toString()
            PidUnit.Kph -> numeric.roundToInt().toString()
            PidUnit.Celsius -> "${numeric.roundToInt()}"
            PidUnit.Percent -> "${numeric.roundToInt()}"
            PidUnit.Volts -> "%.1f".format(numeric)
            PidUnit.Seconds -> numeric.roundToInt().toString()
            PidUnit.Lambda -> "%.3f".format(numeric)
            PidUnit.GramsPerSec -> "%.2f".format(numeric)
            PidUnit.Kpa -> numeric.roundToInt().toString()
            PidUnit.None -> numeric.toString()
        }
    }

    fun displayLabel(value: ParsedPidValue): String {
        val formatted = format(value)
        if (value.pid == FuelSystemStatus.PID) return formatted
        val unit = value.unit.symbol
        return if (unit.isEmpty()) formatted else "$formatted $unit"
    }
}
