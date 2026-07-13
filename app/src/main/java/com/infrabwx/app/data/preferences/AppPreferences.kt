package com.infrabwx.app.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "app_preferences")

class AppPreferences(private val context: Context) {

    companion object {
        private val TERMS_ACCEPTED = booleanPreferencesKey("terms_accepted")
        private val THEME_MODE = stringPreferencesKey("theme_mode")
    }

    val termsAccepted: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[TERMS_ACCEPTED] ?: false
    }

    val themeMode: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[THEME_MODE] ?: "auto"
    }

    suspend fun setTermsAccepted(accepted: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[TERMS_ACCEPTED] = accepted
        }
    }

    suspend fun setThemeMode(mode: String) {
        context.dataStore.edit { prefs ->
            prefs[THEME_MODE] = mode
        }
    }
}
