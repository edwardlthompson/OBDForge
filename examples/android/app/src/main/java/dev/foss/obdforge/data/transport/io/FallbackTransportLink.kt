package dev.foss.obdforge.data.transport.io

class FallbackTransportLink(
    private val links: List<TransportLink>,
) : TransportLink {
    private var active: TransportLink? = null

    override val isOpen: Boolean
        get() = active?.isOpen == true

    override suspend fun open(): Result<Unit> {
        var lastError: Throwable? = null
        for (link in links) {
            link.open().fold(
                onSuccess = {
                    active = link
                    return Result.success(Unit)
                },
                onFailure = {
                    lastError = it
                    link.close()
                },
            )
        }
        return Result.failure(lastError ?: IllegalStateException("No transport link opened"))
    }

    override suspend fun close() {
        active?.close()
        active = null
    }

    override suspend fun write(bytes: ByteArray): Result<Unit> {
        val link = active ?: return Result.failure(IllegalStateException("Link not open"))
        return link.write(bytes)
    }

    override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> {
        val link = active ?: return Result.failure(IllegalStateException("Link not open"))
        return link.readAvailable(timeoutMs)
    }
}
