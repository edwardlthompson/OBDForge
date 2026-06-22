package dev.foss.obdforge.domain.diagnostics

import dev.foss.obdforge.domain.livedata.ParsedPidValue
import dev.foss.obdforge.feature.livedata.logic.PidFormatter

object PidRangeEvaluator {
    private data class Range(val min: Double, val max: Double)

    private val ranges: Map<Int, Range> = mapOf(
        0x05 to Range(min = 60.0, max = 105.0),
        0x0F to Range(min = -40.0, max = 80.0),
        0x42 to Range(min = 11.5, max = 15.0),
        0x2F to Range(min = 5.0, max = 100.0),
        0x5C to Range(min = 70.0, max = 130.0),
        0x46 to Range(min = -40.0, max = 55.0),
    )

    fun evaluateAll(parsed: List<ParsedPidValue>): List<AbnormalPidReading> {
        val byPid = parsed.associateBy { it.pid }
        val results = parsed.mapNotNull(::evaluateSingle).toMutableList()
        val rpm = byPid[0x0C]?.numericValue
        val speed = byPid[0x0D]?.numericValue
        if (rpm != null && speed != null && speed < 8.0 && rpm > 3500.0) {
            byPid[0x0C]?.let { sample ->
                results += abnormal(
                    sample = sample,
                    reason = AbnormalReason.AboveMax,
                )
            }
        }
        return results.distinctBy { it.pid }
    }

    private fun evaluateSingle(sample: ParsedPidValue): AbnormalPidReading? {
        val value = sample.numericValue ?: return null
        val range = ranges[sample.pid] ?: return null
        return when {
            value < range.min -> abnormal(sample, AbnormalReason.BelowMin)
            value > range.max -> abnormal(sample, AbnormalReason.AboveMax)
            else -> null
        }
    }

    private fun abnormal(sample: ParsedPidValue, reason: AbnormalReason): AbnormalPidReading =
        AbnormalPidReading(
            pid = sample.pid,
            name = sample.name,
            formattedValue = PidFormatter.displayLabel(sample),
            unit = sample.unit,
            numericValue = requireNotNull(sample.numericValue),
            reason = reason,
        )
}
