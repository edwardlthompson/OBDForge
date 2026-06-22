package dev.foss.obdforge.domain.protocol

data class DtcEntry(
    val code: String,
    val raw: String,
)

data class DtcList(
    val entries: List<DtcEntry>,
    val raw: String,
)
