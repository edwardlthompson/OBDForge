package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.welcomeDataStore: DataStore<Preferences> by preferencesDataStore(
    name = "welcome_preferences",
)

class WelcomePreferences(private val context: Context) {
    val completed: Flow<Boolean> = context.welcomeDataStore.data.map { prefs ->
        prefs[COMPLETED_KEY] ?: false
    }

    suspend fun setCompleted(completed: Boolean) {
        context.welcomeDataStore.edit { prefs ->
            prefs[COMPLETED_KEY] = completed
        }
    }

    companion object {
        private val COMPLETED_KEY = booleanPreferencesKey("welcome_completed")
    }
}
