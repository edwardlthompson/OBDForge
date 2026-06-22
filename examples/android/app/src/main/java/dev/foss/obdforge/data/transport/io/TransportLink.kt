package dev.foss.obdforge.data.transport.io

interface TransportLink {
    val isOpen: Boolean

    suspend fun open(): Result<Unit>
    suspend fun close()
    suspend fun write(bytes: ByteArray): Result<Unit>
    suspend fun readAvailable(timeoutMs: Long): Result<ByteArray>
}
