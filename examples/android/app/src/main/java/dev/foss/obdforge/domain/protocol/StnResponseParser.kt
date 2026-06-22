package dev.foss.obdforge.domain.protocol

object StnResponseParser {
    private val STN_ID_PATTERN = Regex("""STN(\d{4})""", RegexOption.IGNORE_CASE)

    fun looksLikeStn(identifyResponse: String): Boolean =
        STN_ID_PATTERN.containsMatchIn(identifyResponse)

    fun parseCapabilities(stiResponse: String, stixResponse: String? = null): StnCapabilities? {
        val match = STN_ID_PATTERN.find(stiResponse) ?: return null
        val chipId = match.groupValues[1]
        val version = VERSION_PATTERN.find(stiResponse)?.groupValues?.get(1).orEmpty()
        val stpxCapable = supportsStpxChip(chipId) && !stixIndicatesLegacy(stixResponse)
        return StnCapabilities(
            chipId = chipId,
            firmwareVersion = version,
            supportsStpx = stpxCapable,
            supportsBatchedCommands = stpxCapable,
        )
    }

    fun supportsStpxChip(chipId: String): Boolean {
        val series = chipId.take(2)
        return series == "21" || series == "22"
    }

    fun looksLikeStpxProbeResponse(response: String): Boolean {
        val normalized = response.trim()
        if (normalized == "?") return false
        return ObdIsoResponseParser.normalizeHexForTest(response).contains("41")
    }

    private fun stixIndicatesLegacy(stixResponse: String?): Boolean =
        stixResponse?.contains("STN11", ignoreCase = true) == true

    private val VERSION_PATTERN = Regex("""v(\d+\.\d+\.\d+)""", RegexOption.IGNORE_CASE)
}
