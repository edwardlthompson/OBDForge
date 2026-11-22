package dev.foss.obdforge.data.demo

/**
 * Three deterministic OBD command/response sessions for [MockSessionTransport].
 * Used only when the debug "canned session replay" setting is enabled.
 */
data class CannedSession(
    val id: String,
    val label: String,
    val responses: Map<String, String>,
) {
    fun responseFor(command: String): String {
        val key = command.trim().uppercase()
        return responses[key] ?: responses[key.replace(" ", "")] ?: "OK"
    }
}

object CannedSessions {
    const val DEFAULT_ID: String = "idle_p0133"

    val idleP0133: CannedSession = CannedSession(
        id = DEFAULT_ID,
        label = "Idle · P0133",
        responses = mapOf(
            "ATZ" to "ELM327 v2.3",
            "ATI" to "ELM327 v2.3 (canned idle)",
            "ATE0" to "OK",
            "ATL0" to "OK",
            "ATSP0" to "OK",
            "0902" to "49 02 01 31 47 31 4A 43 35 34 34 32 52 37 32 35 31 32 33 34",
            "03" to "43 01 33 01 71 00 00 00",
            "07" to "47 01 33 00 00 00 00 00",
            "010C" to "41 0C 0C 80",
            "010D" to "41 0D 00",
            "0105" to "41 05 64",
            "0114" to "41 14 48 FF",
        ),
    )

    val highwayClean: CannedSession = CannedSession(
        id = "highway_clean",
        label = "Highway · clean",
        responses = mapOf(
            "ATZ" to "STN1101 v3.0.0",
            "ATI" to "OBDLink MX+ (canned highway)",
            "ATE0" to "OK",
            "ATL0" to "OK",
            "ATSP0" to "OK",
            "0902" to "49 02 01 31 46 4D 48 55 36 4A 58 31 58 4C 42 35 34 31 38",
            "03" to "43 00",
            "07" to "47 00",
            "010C" to "41 0C 1F 40",
            "010D" to "41 0D 46",
            "0105" to "41 05 7A",
            "0111" to "41 11 C0",
        ),
    )

    val coldStartPending: CannedSession = CannedSession(
        id = "cold_pending",
        label = "Cold start · pending",
        responses = mapOf(
            "ATZ" to "ELM327 v1.5",
            "ATI" to "ELM327 v1.5 (canned cold)",
            "ATE0" to "OK",
            "ATL0" to "OK",
            "ATSP0" to "OK",
            "0902" to "49 02 01 32 54 31 42 55 34 45 45 38 41 35 30 30 30 31 32",
            "03" to "43 00",
            "07" to "47 02 03 00 01 71 00 00",
            "010C" to "41 0C 09 60",
            "010D" to "41 0D 00",
            "0105" to "41 05 3C",
            "010F" to "41 0F 2D",
        ),
    )

    val all: List<CannedSession> = listOf(idleP0133, highwayClean, coldStartPending)

    fun byId(id: String): CannedSession =
        all.firstOrNull { it.id == id } ?: idleP0133
}
