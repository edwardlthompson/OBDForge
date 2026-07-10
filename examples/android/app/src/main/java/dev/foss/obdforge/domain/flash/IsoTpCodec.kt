package dev.foss.obdforge.domain.flash

/**
 * ISO-TP (ISO 15765-2) framing helpers for UDS payloads.
 * Stage A: encode/decode single- and multi-frame PCI without bus I/O.
 */
object IsoTpCodec {
    const val DEFAULT_MAX_FRAME_DATA = 7

    fun encode(payload: ByteArray, maxFrameData: Int = DEFAULT_MAX_FRAME_DATA): List<ByteArray> {
        require(maxFrameData in 1..7) { "maxFrameData must be 1..7 for classic CAN" }
        if (payload.isEmpty()) return listOf(byteArrayOf(0x00))
        if (payload.size <= maxFrameData) {
            val frame = ByteArray(1 + payload.size)
            frame[0] = payload.size.toByte()
            payload.copyInto(frame, 1)
            return listOf(frame)
        }
        val frames = mutableListOf<ByteArray>()
        val firstData = maxFrameData - 1
        val first = ByteArray(1 + 1 + firstData)
        first[0] = (0x10 or ((payload.size shr 8) and 0x0F)).toByte()
        first[1] = (payload.size and 0xFF).toByte()
        payload.copyInto(first, 2, 0, firstData)
        frames.add(first)
        var offset = firstData
        var seq = 1
        while (offset < payload.size) {
            val chunk = minOf(maxFrameData, payload.size - offset)
            val cf = ByteArray(1 + chunk)
            cf[0] = (0x20 or (seq and 0x0F)).toByte()
            payload.copyInto(cf, 1, offset, offset + chunk)
            frames.add(cf)
            offset += chunk
            seq = (seq + 1) and 0x0F
        }
        return frames
    }

    fun decode(frames: List<ByteArray>): ByteArray {
        if (frames.isEmpty()) return ByteArray(0)
        val first = frames.first()
        if (first.isEmpty()) return ByteArray(0)
        val pciType = (first[0].toInt() shr 4) and 0x0F
        return when (pciType) {
            0 -> {
                val len = first[0].toInt() and 0x0F
                if (len == 0 || first.size < 1 + len) ByteArray(0)
                else first.copyOfRange(1, 1 + len)
            }
            1 -> decodeMulti(frames)
            else -> ByteArray(0)
        }
    }

    private fun decodeMulti(frames: List<ByteArray>): ByteArray {
        val first = frames.first()
        if (first.size < 2) return ByteArray(0)
        val total = ((first[0].toInt() and 0x0F) shl 8) or (first[1].toInt() and 0xFF)
        val out = ByteArray(total)
        var written = 0
        val firstChunk = first.copyOfRange(2, first.size)
        val n = minOf(firstChunk.size, total)
        firstChunk.copyInto(out, 0, 0, n)
        written = n
        var expectedSeq = 1
        for (i in 1 until frames.size) {
            if (written >= total) break
            val frame = frames[i]
            if (frame.isEmpty()) continue
            if ((frame[0].toInt() shr 4) and 0x0F != 2) continue
            val seq = frame[0].toInt() and 0x0F
            if (seq != expectedSeq) return ByteArray(0)
            val chunk = frame.copyOfRange(1, frame.size)
            val take = minOf(chunk.size, total - written)
            chunk.copyInto(out, written, 0, take)
            written += take
            expectedSeq = (expectedSeq + 1) and 0x0F
        }
        return if (written == total) out else ByteArray(0)
    }

    fun flowControlContinue(blockSize: Int = 0, stMin: Int = 0): ByteArray =
        byteArrayOf(0x30, (blockSize and 0xFF).toByte(), (stMin and 0xFF).toByte())
}
