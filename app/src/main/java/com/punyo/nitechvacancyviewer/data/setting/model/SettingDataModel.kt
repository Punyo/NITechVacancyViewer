package com.punyo.nitechvacancyviewer.data.setting.model

data class SettingLocalDataModel(
    val themeSettings: ThemeSettings = ThemeSettings.LIGHT,
)

enum class ThemeSettings {
    LIGHT,
    DARK,
    SYSTEM,
}
