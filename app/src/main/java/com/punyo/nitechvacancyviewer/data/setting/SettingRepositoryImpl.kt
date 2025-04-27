package com.punyo.nitechvacancyviewer.data.setting

import android.content.Context
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import com.punyo.nitechvacancyviewer.data.setting.source.SettingLocalDataSource
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl
    @Inject
    constructor(
        private val applicationContext: Context,
        private val settingLocalDataSource: SettingLocalDataSource,
    ) : SettingRepository {
        override val themeSettings: Flow<ThemeSettings> by lazy {
            settingLocalDataSource.loadThemeSetting(applicationContext)
        }

        override suspend fun saveThemeSetting(themeSettings: ThemeSettings) {
            settingLocalDataSource.saveThemeSetting(applicationContext, themeSettings)
        }
    }
