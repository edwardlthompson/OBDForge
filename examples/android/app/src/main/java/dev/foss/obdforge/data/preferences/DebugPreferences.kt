package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.obdforge.data.demo.CannedSessions
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.debugDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "debug_preferences",
)

/**
 * Debug-only transport preferences. UI is gated by [BuildConfig.DEBUG]; values are ignored
 * in release builds when the settings toggle is hidden and factory defaults stay off.
 */
class DebugPreferences(private val context: Context) {
    val useMockTransport: Flow<Boolean> = context.debugDataStore.data.map { prefs ->
        prefs[USE_MOCK_KEY] ?: DEFAULT_USE_MOCK
    }

    val mockSessionId: Flow<String> = context.debugDataStore.data.map { prefs ->
        prefs[SESSION_ID_KEY] ?: CannedSessions.DEFAULT_ID
    }

    suspend fun setUseMockTransport(enabled: Boolean) {
        context.debugDataStore.edit { prefs ->
            prefs[USE_MOCK_KEY] = enabled
        }
    }

    suspend fun setMockSessionId(sessionId: String) {
        context.debugDataStore.edit { prefs ->
            prefs[SESSION_ID_KEY] = sessionId
        }
    }

    companion object {
        private val USE_MOCK_KEY = booleanPreferencesKey("use_mock_transport")
        private val SESSION_ID_KEY = stringPreferencesKey("mock_session_id")
        const val DEFAULT_USE_MOCK: Boolean = false
    }
}
