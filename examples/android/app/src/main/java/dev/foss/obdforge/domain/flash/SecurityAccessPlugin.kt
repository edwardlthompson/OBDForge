package dev.foss.obdforge.domain.flash

/**
 * User-supplied security access. Default APK ships no OEM seed/key algorithms.
 * Local AI may match installed plugin names ([FlashSecurityAssist]) but must never invent keys.
 */
fun interface SecurityAccessPlugin {
    fun keyFor(seed: ByteArray, level: Int, ecuId: String): Result<ByteArray>
}

/** Explicit reject — used until the user installs a plugin. */
object RejectingSecurityAccessPlugin : SecurityAccessPlugin {
    override fun keyFor(seed: ByteArray, level: Int, ecuId: String): Result<ByteArray> =
        Result.failure(
            IllegalStateException(
                "Security access (27) requires a user-supplied plugin or manual key — " +
                    "local AI cannot generate keys",
            ),
        )
}

/** Expert path: paste a precomputed key (even-length hex). */
class ManualKeySecurityAccessPlugin(
    private val keyHex: String,
) : SecurityAccessPlugin {
    override fun keyFor(seed: ByteArray, level: Int, ecuId: String): Result<ByteArray> {
        val clean = keyHex.filter { it.isDigit() || it in 'A'..'F' || it in 'a'..'f' }
        if (clean.isEmpty() || clean.length % 2 != 0) {
            return Result.failure(IllegalArgumentException("Manual key must be even-length hex"))
        }
        val key = ByteArray(clean.length / 2) { i ->
            clean.substring(i * 2, i * 2 + 2).toInt(16).toByte()
        }
        return Result.success(key)
    }
}

/**
 * Fixture-only unlock for the demo ECU profile. Not an OEM algorithm.
 * Production vehicles still require a user-supplied plugin or manual key.
 */
object DemoSecurityAccessPlugin : SecurityAccessPlugin {
    override fun keyFor(seed: ByteArray, level: Int, ecuId: String): Result<ByteArray> {
        if (!ecuId.startsWith("demo", ignoreCase = true)) {
            return Result.failure(
                IllegalStateException("Demo security plugin only unlocks demo-* ECU profiles"),
            )
        }
        // Deterministic fixture key derived from seed length — not cryptanalysis.
        return Result.success(byteArrayOf(0xDE.toByte(), 0xAD.toByte(), 0xBE.toByte(), 0xEF.toByte()))
    }
}

object SecurityAccessRegistry {
    @Volatile
    private var plugin: SecurityAccessPlugin = RejectingSecurityAccessPlugin

    fun install(plugin: SecurityAccessPlugin) {
        this.plugin = plugin
    }

    fun reset() {
        plugin = RejectingSecurityAccessPlugin
    }

    fun current(): SecurityAccessPlugin = plugin
}
