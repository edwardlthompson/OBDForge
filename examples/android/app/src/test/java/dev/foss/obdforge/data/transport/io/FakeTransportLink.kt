package dev.foss.obdforge.data.transport.io

class FakeTransportLink(
    private val responses: List<String> = listOf("OK\r>"),
) : TransportLink {
    val writes = mutableListOf<String>()
    private var responseIndex = 0
    private var open = false

    override val isOpen: Boolean
        get() = open

    override suspend fun open(): Result<Unit> {
        open = true
        return Result.success(Unit)
    }

    override suspend fun close() {
        open = false
    }

    override suspend fun write(bytes: ByteArray): Result<Unit> {
        writes.add(bytes.toString(Charsets.US_ASCII))
        return Result.success(Unit)
    }

    override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> {
        val next = responses.getOrElse(responseIndex) { "OK\r>" }
        responseIndex++
        return Result.success(next.toByteArray(Charsets.US_ASCII))
    }
}
