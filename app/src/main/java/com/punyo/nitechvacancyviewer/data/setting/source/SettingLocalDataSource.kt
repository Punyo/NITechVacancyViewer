package com.punyo.nitechvacancyviewer.data.setting.source

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class SettingLocalDataSource {
    private val Context.dataStore by preferencesDataStore(name = "settings")
    private val themeSettingsPreferences = intPreferencesKey("theme")
    private val appLaunchCountKey = intPreferencesKey("app_launch_count")
    private val lastReviewRequestEpochDayKey = longPreferencesKey("last_review_request_epoch_day")

    suspend fun saveThemeSetting(
        context: Context,
        themeSettings: ThemeSettings,
    ) {
        val dataStore = context.dataStore
        dataStore.edit { preferences ->
            preferences[themeSettingsPreferences] = themeSettings.ordinal
        }
    }

    fun loadThemeSetting(context: Context): Flow<ThemeSettings> {
        val dataStore = context.dataStore
        return dataStore.data.map { preferences ->
            ThemeSettings.entries[preferences[themeSettingsPreferences] ?: 0]
        }
    }

    /**
     * アプリ起動回数を1増やし、増加後の値を返す。
     */
    suspend fun getAndIncrementLaunchCount(context: Context): Int {
        val dataStore = context.dataStore
        var newCount = 0
        dataStore.edit { preferences ->
            val current = preferences[appLaunchCountKey] ?: 0
            newCount = current + 1
            preferences[appLaunchCountKey] = newCount
        }
        return newCount
    }

    /**
     * 最後にレビューをリクエストした日付 (EpochDay) を返す。未リクエストの場合は -1L を返す。
     */
    suspend fun getLastReviewRequestEpochDay(context: Context): Long {
        val dataStore = context.dataStore
        return dataStore.data.first()[lastReviewRequestEpochDayKey] ?: -1L
    }

    /**
     * 最後にレビューをリクエストした日付 (EpochDay) を保存する。
     */
    suspend fun saveLastReviewRequestEpochDay(
        context: Context,
        epochDay: Long,
    ) {
        val dataStore = context.dataStore
        dataStore.edit { preferences ->
            preferences[lastReviewRequestEpochDayKey] = epochDay
        }
    }
}
