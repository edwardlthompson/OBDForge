package dev.foss.obdforge.domain.protocol

data class PidResponse(
    val mode: ObdMode,
    val pid: Int,
    val payload: ByteArray,
    val raw: String,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PidResponse) return false
        return mode == other.mode && pid == other.pid && payload.contentEquals(other.payload)
    }

    override fun hashCode(): Int = 31 * mode.hashCode() + pid + payload.contentHashCode()
}
