package com.punyo.nitechvacancyviewer.data.setting

import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    val themeSettings: Flow<ThemeSettings>

    suspend fun saveThemeSetting(themeSettings: ThemeSettings)

    /**
     * アプリ起動回数を1増やし、増加後の値を返す。
     */
    suspend fun getAndIncrementLaunchCount(): Int

    /**
     * 最後にレビューをリクエストした日付 (EpochDay) を返す。未リクエストの場合は -1L を返す。
     */
    suspend fun getLastReviewRequestEpochDay(): Long

    /**
     * 最後にレビューをリクエストした日付 (EpochDay) を保存する。
     */
    suspend fun saveLastReviewRequestEpochDay(epochDay: Long)
}
