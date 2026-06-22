package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.diagnosticLogDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "diagnostic_log_preferences",
)

class DiagnosticLogPreferences(private val context: Context) {
    val enabled: Flow<Boolean> = context.diagnosticLogDataStore.data.map { prefs ->
        prefs[ENABLED_KEY] ?: DEFAULT_ENABLED
    }

    suspend fun isEnabled(): Boolean = enabled.first()

    suspend fun setEnabled(enabled: Boolean) {
        context.diagnosticLogDataStore.edit { prefs ->
            prefs[ENABLED_KEY] = enabled
        }
    }

    companion object {
        private val ENABLED_KEY = booleanPreferencesKey("diagnostic_log_enabled")
        const val DEFAULT_ENABLED: Boolean = true
    }
}
