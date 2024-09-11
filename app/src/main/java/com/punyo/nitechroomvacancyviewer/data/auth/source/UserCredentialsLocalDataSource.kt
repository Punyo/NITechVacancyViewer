package com.punyo.nitechroomvacancyviewer.data.auth.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.punyo.nitechroomvacancyviewer.data.auth.model.UserCredentialsDataModel
import kotlinx.coroutines.flow.first

object UserCredentialsLocalDataSource {
    private val USERNAME_PREFERENCE = stringPreferencesKey("username")
    private val PASSWORD_PREFERENCE = stringPreferencesKey("password")
    private val Context.dataStore by preferencesDataStore(name = "credentials")

    suspend fun loadCredentials(context: Context): UserCredentialsDataModel? {
        try {
            val dataStore: DataStore<Preferences> = context.dataStore
            val userName = dataStore.data.first()[USERNAME_PREFERENCE]
            val password = dataStore.data.first()[PASSWORD_PREFERENCE]
            if (userName != null && password != null) {
                return UserCredentialsDataModel(userName, password)
            }
        } catch (e: NoSuchElementException) {
            return null
        }
        return null
    }

    suspend fun saveCredentials(context: Context, userCredentials: UserCredentialsDataModel) {
        val dataStore: DataStore<Preferences> = context.dataStore
        dataStore.edit { preferences ->
            preferences[USERNAME_PREFERENCE] = userCredentials.userName
            preferences[PASSWORD_PREFERENCE] = userCredentials.password
        }
    }
}