package dev.foss.obdforge.domain.safety

object ExpertUnlockPolicy {
    const val UNLOCK_DURATION_MS = 30L * 60L * 1000L

    /** Documented default PIN for expert unlock; expert mode remains off until entered. */
    const val DEFAULT_PIN = "1234"

    fun isValidPin(pin: String): Boolean = pin == DEFAULT_PIN

    fun expiresAt(unlockedAtMs: Long): Long = unlockedAtMs + UNLOCK_DURATION_MS
}
