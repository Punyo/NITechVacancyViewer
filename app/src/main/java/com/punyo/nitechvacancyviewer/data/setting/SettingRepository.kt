package com.punyo.nitechvacancyviewer.data.setting

import android.content.Context
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import com.punyo.nitechvacancyviewer.data.setting.source.SettingLocalDatasource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class SettingRepository(
    private val context: Context
) {
    val themeSettings: Flow<ThemeSettings> by lazy {
        SettingLocalDatasource.loadThemeSetting(context)
    }

    suspend fun saveThemeSetting(themeSettings: ThemeSettings) {
        SettingLocalDatasource.saveThemeSetting(context, themeSettings)
    }
}