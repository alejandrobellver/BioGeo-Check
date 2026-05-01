package com.example.biogeo_check.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

/**
 * Gestión de sesión persistente con DataStore
 */
class SessionManager(private val context: Context) {

    companion object {
        private val KEY_IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        private val KEY_USER_ID = stringPreferencesKey("user_id")
        private val KEY_USER_NAME = stringPreferencesKey("user_name")
        private val KEY_USER_EMAIL = stringPreferencesKey("user_email")
        private val KEY_USER_ROLE = stringPreferencesKey("user_role")
        private val KEY_TOKEN = stringPreferencesKey("token")
    }

    val isLoggedIn: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_IS_LOGGED_IN] ?: false
    }

    val userId: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ID] ?: ""
    }

    val userName: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_NAME] ?: ""
    }

    val userRole: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_USER_ROLE] ?: "empleado"
    }

    suspend fun saveSession(userId: String, name: String, email: String, role: String, token: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_IS_LOGGED_IN] = true
            prefs[KEY_USER_ID] = userId
            prefs[KEY_USER_NAME] = name
            prefs[KEY_USER_EMAIL] = email
            prefs[KEY_USER_ROLE] = role
            prefs[KEY_TOKEN] = token
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { it.clear() }
    }
}
