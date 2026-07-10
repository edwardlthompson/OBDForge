package dev.foss.obdforge.domain.flash

/**
 * Demo / dry-run transfer engine. Stages A: validates profile + binary, builds
 * ISO-TP framed UDS sequence, never auto-retries erase/download on failure.
 */
class FlashTransferEngine(
    private val security: SecurityAccessPlugin = SecurityAccessRegistry.current(),
) {
    data class TransferPlan(
        val profileId: String,
        val fileSha256Hex: String,
        val steps: List<FlashStep>,
        val totalBlocks: Int,
    )

    data class FlashStep(
        val name: String,
        val serviceId: Int,
        val payload: ByteArray,
        val isoTpFrames: List<ByteArray>,
    )

    fun plan(
        profile: EcuFlashProfile,
        binary: ByteArray,
        demoSeed: ByteArray = byteArrayOf(0x12, 0x34),
        allowDemoSecurity: Boolean = false,
    ): Result<TransferPlan> {
        if (!profile.isValid()) {
            return Result.failure(IllegalArgumentException("Incomplete ECU flash profile"))
        }
        if (binary.isEmpty()) {
            return Result.failure(IllegalArgumentException("Firmware binary is empty"))
        }
        if (binary.size > profile.memorySize) {
            return Result.failure(
                IllegalArgumentException("Binary exceeds profile memorySize ${profile.memorySize}"),
            )
        }
        val plugin = if (allowDemoSecurity && profile.id.startsWith("demo", ignoreCase = true)) {
            DemoSecurityAccessPlugin
        } else {
            security
        }
        val key = plugin.keyFor(demoSeed, profile.securityLevel, profile.id).getOrElse {
            return Result.failure(it)
        }
        val steps = mutableListOf<FlashStep>()
        fun add(name: String, service: Int, payload: ByteArray) {
            steps.add(
                FlashStep(
                    name = name,
                    serviceId = service,
                    payload = payload,
                    isoTpFrames = IsoTpLink.frame(payload),
                ),
            )
        }
        add("DiagnosticSessionControl", 0x10, UdsProgrammingSession.enterProgramming(profile.programmingSessionId))
        add("SecurityAccessRequestSeed", 0x27, UdsProgrammingSession.requestSeed(profile.securityLevel))
        add("SecurityAccessSendKey", 0x27, UdsProgrammingSession.sendKey(profile.securityLevel, key))
        add(
            "RequestDownload",
            0x34,
            UdsProgrammingSession.requestDownload(profile.dataFormatId, profile.memoryAddress, binary.size),
        )
        var seq = 1
        var offset = 0
        val blockLen = profile.maxBlockLength.coerceAtLeast(1)
        while (offset < binary.size) {
            val end = minOf(offset + blockLen, binary.size)
            val chunk = binary.copyOfRange(offset, end)
            add("TransferData#$seq", 0x36, UdsProgrammingSession.transferData(seq, chunk))
            offset = end
            seq = (seq % 0xFF) + 1
        }
        val totalBlocks = steps.count { it.serviceId == 0x36 }
        add("RequestTransferExit", 0x37, UdsProgrammingSession.transferExit())
        return Result.success(
            TransferPlan(
                profileId = profile.id,
                fileSha256Hex = sha256Hex(binary),
                steps = steps,
                totalBlocks = totalBlocks,
            ),
        )
    }

    /** Demo execution: returns canned positive responses; never retries on synthetic NACK. */
    fun executeDemo(plan: TransferPlan): Result<List<String>> {
        val responses = mutableListOf<String>()
        for (step in plan.steps) {
            if (step.payload.isEmpty()) {
                return Result.failure(IllegalStateException("Empty flash step ${step.name}"))
            }
            val positive = (step.serviceId + 0x40) and 0xFF
            responses.add("%02X".format(positive))
        }
        return Result.success(responses)
    }

    companion object {
        fun sha256Hex(bytes: ByteArray): String {
            val digest = java.security.MessageDigest.getInstance("SHA-256").digest(bytes)
            return digest.joinToString("") { "%02x".format(it) }
        }
    }
}
