package dev.foss.obdforge.domain.flash

/**
 * Builds UDS programming-session command bytes (Stage A — no bus I/O).
 */
object UdsProgrammingCommands {
    fun diagnosticSessionControl(sessionId: Int): ByteArray =
        byteArrayOf(0x10, (sessionId and 0xFF).toByte())

    fun securityAccessRequestSeed(level: Int): ByteArray =
        byteArrayOf(0x27, (level and 0xFF).toByte())

    fun securityAccessSendKey(level: Int, key: ByteArray): ByteArray =
        byteArrayOf(0x27, ((level + 1) and 0xFF).toByte()) + key

    fun requestDownload(
        dataFormatId: Int,
        address: Long,
        size: Int,
    ): ByteArray {
        // addrAndLengthFormatIdentifier: 4-byte address, 4-byte size
        val alfi = 0x44
        return byteArrayOf(0x34, (dataFormatId and 0xFF).toByte(), alfi.toByte()) +
            int32Be(address.toInt()) +
            int32Be(size)
    }

    fun transferData(blockSequence: Int, data: ByteArray): ByteArray =
        byteArrayOf(0x36, (blockSequence and 0xFF).toByte()) + data

    fun requestTransferExit(): ByteArray = byteArrayOf(0x37)

    fun ecuReset(resetType: Int = 0x01): ByteArray =
        byteArrayOf(0x11, (resetType and 0xFF).toByte())

    fun isNegativeResponse(payload: ByteArray): Boolean =
        payload.isNotEmpty() && payload[0] == 0x7F.toByte()

    private fun int32Be(value: Int): ByteArray = byteArrayOf(
        ((value ushr 24) and 0xFF).toByte(),
        ((value ushr 16) and 0xFF).toByte(),
        ((value ushr 8) and 0xFF).toByte(),
        (value and 0xFF).toByte(),
    )
}
