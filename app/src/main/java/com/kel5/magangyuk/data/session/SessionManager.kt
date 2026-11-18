package com.kel5.magangyuk.data.session

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.kel5.magangyuk.data.local.entity.Role
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

data class Session(val userId: Long, val role: Role)

private val Context.sessionDataStore by preferencesDataStore(name = "session")

class SessionManager(private val context: Context) {

    private object Keys {
        val USER_ID = longPreferencesKey("user_id")
        val ROLE = stringPreferencesKey("role")
        val NOTIF_PERMISSION_ASKED = booleanPreferencesKey("notif_permission_asked")
    }

    val session: Flow<Session?> = context.sessionDataStore.data.map { prefs ->
        val userId = prefs[Keys.USER_ID]
        val roleName = prefs[Keys.ROLE]
        if (userId == null || roleName == null) {
            null
        } else {
            val role = Role.entries.firstOrNull { it.name == roleName }
            if (role == null) null else Session(userId, role)
        }
    }

    val notifPermissionAsked: Flow<Boolean> = context.sessionDataStore.data.map { prefs ->
        prefs[Keys.NOTIF_PERMISSION_ASKED] ?: false
    }

    suspend fun saveSession(userId: Long, role: Role) {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.USER_ID] = userId
            prefs[Keys.ROLE] = role.name
        }
    }

    suspend fun clearSession() {
        context.sessionDataStore.edit { prefs ->
            prefs.remove(Keys.USER_ID)
            prefs.remove(Keys.ROLE)
        }
    }

    suspend fun markNotifPermissionAsked() {
        context.sessionDataStore.edit { prefs ->
            prefs[Keys.NOTIF_PERMISSION_ASKED] = true
        }
    }
}