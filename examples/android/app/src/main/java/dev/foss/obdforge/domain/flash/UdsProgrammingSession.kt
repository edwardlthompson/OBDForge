package dev.foss.obdforge.domain.flash

/**
 * Ordered UDS programming-session builders. No bus I/O; no blind retry of erase/download.
 */
object UdsProgrammingSession {
    fun enterProgramming(sessionId: Int): ByteArray =
        UdsProgrammingCommands.diagnosticSessionControl(sessionId)

    fun requestSeed(level: Int): ByteArray =
        UdsProgrammingCommands.securityAccessRequestSeed(level)

    fun sendKey(level: Int, key: ByteArray): ByteArray =
        UdsProgrammingCommands.securityAccessSendKey(level, key)

    fun requestDownload(dataFormatId: Int, address: Long, size: Int): ByteArray =
        UdsProgrammingCommands.requestDownload(dataFormatId, address, size)

    fun transferData(blockSequence: Int, data: ByteArray): ByteArray =
        UdsProgrammingCommands.transferData(blockSequence, data)

    fun transferExit(): ByteArray = UdsProgrammingCommands.requestTransferExit()

    fun reset(resetType: Int = 0x01): ByteArray = UdsProgrammingCommands.ecuReset(resetType)

    fun isNegativeResponse(payload: ByteArray): Boolean =
        UdsProgrammingCommands.isNegativeResponse(payload)

    /** Map NRC byte to catalog text; never auto-retry on NACK. */
    fun explainNrc(nrc: Int): String? = UdsNrcCatalog.explain(nrc)
}
