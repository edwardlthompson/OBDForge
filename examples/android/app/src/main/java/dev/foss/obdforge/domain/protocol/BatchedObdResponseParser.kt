package dev.foss.obdforge.domain.protocol

object BatchedObdResponseParser {
    fun parseMode01Batch(response: String, pids: List<Int>): List<PidResponse> {
        val segments = splitSegments(response)
        val parsed = mutableListOf<PidResponse>()
        for (pid in pids) {
            val segment = segments.firstOrNull { segmentContainsPid(it, pid) }
                ?: segments.getOrNull(parsed.size)
                ?: continue
            ObdIsoResponseParser.parseMode01(segment, pid)?.let { parsed.add(it) }
        }
        return parsed
    }

    private fun splitSegments(response: String): List<String> =
        response.split('|', '\n', '\r')
            .map { it.trim() }
            .filter { it.isNotEmpty() && !it.equals("OK", ignoreCase = true) }

    private fun segmentContainsPid(segment: String, pid: Int): Boolean {
        val hex = ObdIsoResponseParser.normalizeHexForTest(segment)
        val prefix = "41" + pid.toString(16).uppercase().padStart(2, '0')
        return hex.contains(prefix)
    }
}
