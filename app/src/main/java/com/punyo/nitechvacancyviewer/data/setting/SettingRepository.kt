package com.punyo.nitechvacancyviewer.data.setting

import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    val themeSettings: Flow<ThemeSettings>

    suspend fun saveThemeSetting(themeSettings: ThemeSettings)
}
