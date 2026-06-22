package dev.foss.obdforge.data.transport.io

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.io.OutputStream
import java.net.InetSocketAddress
import java.net.Socket

class TcpTransportLink(
    private val host: String,
    private val port: Int,
    private val connectTimeoutMs: Int = 5_000,
) : TransportLink {
    private var socket: Socket? = null
    private var input: InputStream? = null
    private var output: OutputStream? = null

    override val isOpen: Boolean
        get() = socket?.isConnected == true && socket?.isClosed == false

    override suspend fun open(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val active = Socket()
            active.connect(InetSocketAddress(host, port), connectTimeoutMs)
            socket = active
            input = active.getInputStream()
            output = active.getOutputStream()
        }
    }

    override suspend fun close() = withContext(Dispatchers.IO) {
        runCatching {
            input?.close()
            output?.close()
            socket?.close()
        }
        input = null
        output = null
        socket = null
    }

    override suspend fun write(bytes: ByteArray): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val stream = requireNotNull(output) { "TCP link not open" }
            stream.write(bytes)
            stream.flush()
        }
    }

    override suspend fun readAvailable(timeoutMs: Long): Result<ByteArray> = withContext(Dispatchers.IO) {
        runCatching {
            val stream = requireNotNull(input) { "TCP link not open" }
            val active = requireNotNull(socket) { "TCP link not open" }
            active.soTimeout = timeoutMs.coerceAtLeast(1).toInt()
            val buffer = ByteArray(512)
            val read = stream.read(buffer)
            if (read <= 0) {
                ByteArray(0)
            } else {
                buffer.copyOf(read)
            }
        }
    }
}
