package dev.foss.obdforge.domain.flash

/**
 * Optional local AI / assist for flash security — match installed plugins and explain NRCs.
 * Never invents seed→key material or cracks firmware crypto.
 */
data class SecurityPluginDescriptor(
    val pluginId: String,
    val displayName: String,
    /** Simple prefix or exact ECU id patterns the user-installed pack claims to cover. */
    val ecuIdPatterns: List<String>,
)

object SecurityAccessPluginCatalog {
    @Volatile
    private var installed: List<SecurityPluginDescriptor> = emptyList()

    fun install(descriptors: List<SecurityPluginDescriptor>) {
        installed = descriptors.toList()
    }

    fun register(descriptor: SecurityPluginDescriptor) {
        installed = installed + descriptor
    }

    fun reset() {
        installed = emptyList()
    }

    fun all(): List<SecurityPluginDescriptor> = installed

    fun matchForEcu(ecuId: String): List<SecurityPluginDescriptor> {
        if (ecuId.isBlank()) return emptyList()
        val id = ecuId.trim()
        return installed.filter { desc ->
            desc.ecuIdPatterns.any { pattern -> matchesPattern(id, pattern) }
        }
    }

    private fun matchesPattern(ecuId: String, pattern: String): Boolean {
        val p = pattern.trim()
        if (p.isEmpty()) return false
        if (p.endsWith("*")) {
            return ecuId.startsWith(p.dropLast(1), ignoreCase = true)
        }
        return ecuId.equals(p, ignoreCase = true)
    }
}

object FlashAiPolicy {
    private val forbidden = listOf(
        "crack",
        "generate key",
        "invent key",
        "derive key",
        "brute",
        "break encryption",
        "decrypt firmware",
        "seed to key",
        "seed→key",
        "compute the key from",
    )

    fun isKeyGenerationRequest(userText: String): Boolean {
        val lower = userText.lowercase()
        return forbidden.any { it in lower }
    }

    fun refuseKeyGenerationMessage(): String =
        "Local AI cannot generate UDS security keys or crack ECU encryption. " +
            "Install a user-supplied SecurityAccessPlugin for a known algorithm, " +
            "or enter a key computed offline."
}

/**
 * Deterministic assist: plugin catalog match + NRC explain. No crypto.
 */
object FlashSecurityAssist {
    fun matchInstalledPlugin(ecuId: String): List<SecurityPluginDescriptor> =
        SecurityAccessPluginCatalog.matchForEcu(ecuId)

    fun explainNrc(nrc: Int): String = UdsNrcCatalog.format(nrc)

    /**
     * Assist entry for UI / optional LLM wrapper. Key-generation prompts fail closed.
     */
    fun assist(userText: String, ecuId: String? = null, nrc: Int? = null): Result<String> {
        if (FlashAiPolicy.isKeyGenerationRequest(userText)) {
            return Result.failure(IllegalArgumentException(FlashAiPolicy.refuseKeyGenerationMessage()))
        }
        val parts = mutableListOf<String>()
        if (nrc != null) {
            parts.add(explainNrc(nrc))
        }
        if (!ecuId.isNullOrBlank()) {
            val matches = matchInstalledPlugin(ecuId)
            parts.add(
                if (matches.isEmpty()) {
                    "No installed security plugin matches ECU id \"$ecuId\". " +
                        "Install a named algorithm pack or use manual key entry."
                } else {
                    "Matching installed plugins: " +
                        matches.joinToString { "${it.displayName} (${it.pluginId})" }
                },
            )
        }
        if (parts.isEmpty()) {
            return Result.success(
                "Flash assist can match installed plugins by ECU id or explain UDS NRCs. " +
                    FlashAiPolicy.refuseKeyGenerationMessage(),
            )
        }
        return Result.success(parts.joinToString("\n"))
    }
}
