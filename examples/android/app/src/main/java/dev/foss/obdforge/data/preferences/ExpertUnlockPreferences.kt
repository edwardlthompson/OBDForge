package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.obdforge.domain.safety.ExpertUnlockPolicy
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.expertUnlockDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "expert_unlock_preferences",
)

class ExpertUnlockPreferences(private val context: Context) {
    val unlockExpiresAtMs: Flow<Long?> = context.expertUnlockDataStore.data.map { prefs ->
        prefs[UNLOCK_EXPIRES_AT_KEY]
    }

    suspend fun unlock(pin: String, nowMs: Long = System.currentTimeMillis()): Boolean {
        if (!ExpertUnlockPolicy.isValidPin(pin)) return false
        val expiresAt = ExpertUnlockPolicy.expiresAt(nowMs)
        context.expertUnlockDataStore.edit { prefs ->
            prefs[UNLOCK_EXPIRES_AT_KEY] = expiresAt
        }
        return true
    }

    suspend fun clearUnlock() {
        context.expertUnlockDataStore.edit { prefs ->
            prefs.remove(UNLOCK_EXPIRES_AT_KEY)
        }
    }

    fun isUnlocked(expiresAtMs: Long?, nowMs: Long = System.currentTimeMillis()): Boolean {
        if (expiresAtMs == null) return false
        return nowMs < expiresAtMs
    }

    companion object {
        private val UNLOCK_EXPIRES_AT_KEY = longPreferencesKey("unlock_expires_at_ms")
    }
}
