package com.unap.aplicaciontareasfinal.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.unap.aplicaciontareasfinal.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

class UserDataStore(context: Context) {

    private val dataStore = context.dataStore

    companion object {
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_WHATSAPP = stringPreferencesKey("user_whatsapp")
        val USER_DATE = stringPreferencesKey("user_date")
    }

    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_NAME] = user.nombre
            preferences[USER_EMAIL] = user.email
            preferences[USER_WHATSAPP] = user.numeroWhatsapp
            preferences[USER_DATE] = user.date
        }
    }

    val getUser: Flow<User?> = dataStore.data.map { preferences ->
        val id = preferences[USER_ID]
        val name = preferences[USER_NAME]
        val email = preferences[USER_EMAIL]
        val whatsapp = preferences[USER_WHATSAPP]
        val date = preferences[USER_DATE]

        if (id != null && name != null && email != null && whatsapp != null && date != null) {
            User(id, name, email, whatsapp, date)
        } else {
            null
        }
    }

    suspend fun clearData() {
        dataStore.edit {
            it.clear()
        }
    }
}
