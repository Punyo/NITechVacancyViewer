package com.punyo.nitechvacancyviewer.ui.widget

import com.punyo.nitechvacancyviewer.data.widget.WidgetRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Glance ウィジェット内で Hilt の DI を使用するための EntryPoint。
 * GlanceAppWidget は ViewModel を持たないため、EntryPointAccessors.fromApplication() で DI を解決する。
 */
@EntryPoint
@InstallIn(SingletonComponent::class)
interface StampWidgetEntryPoint {
    fun widgetRepository(): WidgetRepository
}
