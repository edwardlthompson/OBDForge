package dev.foss.obdforge.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dev.foss.obdforge.domain.livedata.PersonaMode
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.personaDataStore: DataStore<Preferences> by preferencesDataStore(name = "persona_preferences")

class PersonaPreferences(private val context: Context) {
    val persona: Flow<PersonaMode> = context.personaDataStore.data.map { prefs ->
        val raw = prefs[PERSONA_KEY] ?: PersonaMode.Diy.name
        runCatching { PersonaMode.valueOf(raw) }.getOrDefault(PersonaMode.Diy)
    }

    suspend fun setPersona(mode: PersonaMode) {
        context.personaDataStore.edit { prefs ->
            prefs[PERSONA_KEY] = mode.name
        }
    }

    companion object {
        private val PERSONA_KEY = stringPreferencesKey("persona_mode")
    }
}
