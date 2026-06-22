package dev.foss.obdforge.domain.protocol

data class StnCapabilities(
    val chipId: String,
    val firmwareVersion: String,
    val supportsStpx: Boolean,
    val supportsBatchedCommands: Boolean,
)
