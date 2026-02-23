package com.punyo.nitechvacancyviewer.ui.widget

import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

/**
 * StampWidget のライフサイクルイベントを処理する BroadcastReceiver。
 * GlanceAppWidgetReceiver を継承し、ウィジェットの UPDATE / DELETE / ENABLED / DISABLED
 * などのライフサイクル管理を Android フレームワークに委譲する。
 */
class StampWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = StampWidget()
}
