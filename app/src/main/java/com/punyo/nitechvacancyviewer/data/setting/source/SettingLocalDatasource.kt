package com.punyo.nitechvacancyviewer.data.setting.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

object SettingLocalDatasource {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val THEME_SETTING_PREFERENCE = intPreferencesKey("theme")

    suspend fun saveThemeSetting(context: Context, themeSettings: ThemeSettings) {
        val dataStore = context.dataStore
        dataStore.edit { preferences ->
            preferences[THEME_SETTING_PREFERENCE] = themeSettings.ordinal
        }
    }

    fun loadThemeSetting(context: Context): Flow<ThemeSettings> {
        val dataStore = context.dataStore
        return dataStore.data.map { preferences ->
            ThemeSettings.entries[preferences[THEME_SETTING_PREFERENCE] ?: 0]
        }
    }
}