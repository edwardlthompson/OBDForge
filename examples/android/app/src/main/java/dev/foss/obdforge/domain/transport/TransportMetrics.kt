package dev.foss.obdforge.domain.transport

data class TransportMetrics(
    val bytesWritten: Long = 0,
    val bytesRead: Long = 0,
    val connectLatencyMs: Long? = null,
)
