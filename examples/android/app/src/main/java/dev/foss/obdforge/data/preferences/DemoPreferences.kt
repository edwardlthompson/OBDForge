package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.demoDataStore: DataStore<Preferences> by preferencesDataStore(name = "demo_preferences")

class DemoPreferences(private val context: Context) {
    val enabled: Flow<Boolean> = context.demoDataStore.data.map { prefs ->
        prefs[DEMO_ENABLED_KEY] ?: DEFAULT_ENABLED
    }

    suspend fun setEnabled(enabled: Boolean) {
        context.demoDataStore.edit { prefs ->
            prefs[DEMO_ENABLED_KEY] = enabled
        }
    }

    companion object {
        private val DEMO_ENABLED_KEY = booleanPreferencesKey("demo_mode_enabled")
        const val DEFAULT_ENABLED: Boolean = true
    }
}
