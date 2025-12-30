package com.unap.aplicaciontareasfinal.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.unap.aplicaciontareasfinal.data.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Esta es una propiedad de extension para `Context` que crea y gestiona
// una instancia de `DataStore<Preferences>` para las preferencias del usuario.
// `preferencesDataStore` es la forma recomendada de obtener una instancia de DataStore.
// El nombre "user_prefs" es el nombre del archivo donde se guardaran las preferencias.
private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")

/**
 * Esta clase se encarga de almacenar y recuperar las preferencias del usuario localmente
 * utilizando Jetpack DataStore.
 *
 * `Jetpack DataStore` es la solucion de Google para el almacenamiento de datos pequeños y simples,
 * ofreciendo un enfoque asincrono y basado en `Flow` que es mas seguro y robusto que `SharedPreferences`.
 *
 * @param context El contexto de la aplicacion, necesario para inicializar DataStore.
 */
class UserDataStore(context: Context) {

    private val dataStore = context.dataStore

    /**
     * `companion object` en Kotlin se usa para declarar miembros estaticos.
     * Aqui se definen las "keys" (claves) que se usan para almacenar y recuperar valores en DataStore.
     * Cada clave esta tipada (`intPreferencesKey`, `stringPreferencesKey`, etc.) para asegurar
     * que los datos se guarden y recuperen con el tipo correcto.
     */
    companion object {
        val USER_ID = intPreferencesKey("user_id")
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_EMAIL = stringPreferencesKey("user_email")
        val USER_WHATSAPP = stringPreferencesKey("user_whatsapp")
        val USER_DATE = stringPreferencesKey("user_date")
        val HAS_SEEN_ONBOARDING = booleanPreferencesKey("has_seen_onboarding")
    }

    /**
     * Guarda la informacion completa de un objeto `User` en DataStore.
     *
     * `dataStore.edit { preferences -> ... }` es la forma transaccional y asincrona de modificar
     * los datos en DataStore. El bloque `edit` garantiza la atomicidad de la operacion.
     *
     * @param user El objeto `User` a guardar.
     */
    suspend fun saveUser(user: User) {
        dataStore.edit { preferences ->
            preferences[USER_ID] = user.id
            preferences[USER_NAME] = user.nombre
            preferences[USER_EMAIL] = user.email
            preferences[USER_WHATSAPP] = user.numeroWhatsapp
            preferences[USER_DATE] = user.date
        }
    }

    /**
     * Expone un `Flow` de Kotlin que emite el `User` actualmente guardado.
     *
     * Un `Flow` es una secuencia asincrona de valores. Permite a los observadores
     * reaccionar a los cambios de datos en tiempo real.
     *
     * @return Un `Flow<User?>` que emite el objeto `User` guardado, o `null` si no hay usuario.
     */
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

    /**
     * Guarda una preferencia booleana indicando si el usuario ya ha visto la pantalla de bienvenida/onboarding.
     *
     * @param hasSeen `true` si ya la ha visto, `false` en caso contrario.
     */
    suspend fun setHasSeenOnboarding(hasSeen: Boolean) {
        dataStore.edit { preferences ->
            preferences[HAS_SEEN_ONBOARDING] = hasSeen
        }
    }

    /**
     * Expone un `Flow` que emite el estado de si el usuario ha visto la pantalla de bienvenida.
     * @return Un `Flow<Boolean>` que emite `true` si ha visto el onboarding, `false` en caso contrario.
     */
    val hasSeenOnboarding: Flow<Boolean> = dataStore.data.map { preferences ->
        preferences[HAS_SEEN_ONBOARDING] ?: false
    }

    /**
     * Elimina todos los datos del usuario guardados en DataStore, efectivamente "cerrando la sesion" localmente.
     */
    suspend fun clearData() {
        dataStore.edit { preferences ->
            preferences.remove(USER_ID)
            preferences.remove(USER_NAME)
            preferences.remove(USER_EMAIL)
            preferences.remove(USER_WHATSAPP)
            preferences.remove(USER_DATE)
        }
    }
}
