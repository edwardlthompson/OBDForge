package dev.foss.obdforge.domain.livedata

import dev.foss.obdforge.domain.protocol.PidResponse

data class ParsedPidValue(
    val pid: Int,
    val name: String,
    val unit: PidUnit,
    val numericValue: Double?,
)

object PidValueParser {
    fun parse(response: PidResponse): ParsedPidValue? {
        val definition = PidCatalog.get(response.pid) ?: return null
        return ParsedPidValue(
            pid = response.pid,
            name = definition.name,
            unit = definition.unit,
            numericValue = definition.decode(response.payload),
        )
    }

    fun parseAll(responses: List<PidResponse>): List<ParsedPidValue> =
        responses.mapNotNull(::parse)
}
