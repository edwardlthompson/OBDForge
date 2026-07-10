package dev.foss.obdforge.domain.flash

/**
 * Per-ECU flash profile. Start with one documented family — not universal OBD-II.
 */
data class EcuFlashProfile(
    val id: String,
    val displayName: String,
    val ecuIdPattern: String,
    val programmingSessionId: Int = 0x02,
    val securityLevel: Int = 0x01,
    val memoryAddress: Long,
    val memorySize: Int,
    val dataFormatId: Int = 0x00,
    val maxBlockLength: Int = 0x0FF0,
    val eraseRoutineId: Int? = null,
    val verifyRoutineId: Int? = null,
) {
    fun isValid(): Boolean =
        id.isNotBlank() &&
            displayName.isNotBlank() &&
            memorySize > 0 &&
            maxBlockLength > 0 &&
            memoryAddress >= 0
}

object DemoEcuFlashProfile {
    const val ASSET_PATH = "flash/demo_ecu_profile.json"

    val profile = EcuFlashProfile(
        id = "demo-isotp-v1",
        displayName = "OBDForge Demo ECU (ISO-TP fixture)",
        ecuIdPattern = "DEMO*",
        programmingSessionId = 0x02,
        securityLevel = 0x01,
        memoryAddress = 0x00080000,
        memorySize = 256,
        dataFormatId = 0x00,
        maxBlockLength = 64,
        eraseRoutineId = 0xFF00,
        verifyRoutineId = 0xFF01,
    )
}
