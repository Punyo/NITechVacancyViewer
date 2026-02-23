package com.punyo.nitechvacancyviewer.widget.stamp

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import com.punyo.nitechvacancyviewer.AccessibilityConsentActivity
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.widget.model.LaunchError
import com.punyo.nitechvacancyviewer.data.widget.model.LaunchException
import dagger.hilt.android.EntryPointAccessors

/**
 * ウィジェットタップ時に実行されるアクションコールバック。
 * Hilt EntryPoint 経由で WidgetRepository を取得し、打刻アプリの起動を行う。
 *
 * 起動フロー:
 * 1. ローディング状態に遷移し、ウィジェット UI を更新
 * 2. WidgetRepository.launchStampApp() で打刻アプリを起動
 * 3. 成功時は何もしない（AccessibilityService が自動操作）
 * 4. 失敗時はエラー種別に応じた Toast を表示
 * 5. アイドル状態に戻り、ウィジェット UI を更新
 */
class OnWidgetTapCallback : ActionCallback {
    companion object {
        val isLaunchingKey = booleanPreferencesKey("is_launching")
    }

    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters,
    ) {
        val entryPoint =
            EntryPointAccessors.fromApplication(
                context.applicationContext,
                StampWidgetEntryPoint::class.java,
            )

        val repository = entryPoint.widgetRepository()
        if (!repository.isAccessibilityServiceEnabled(context)) {
            context.startActivity(
                Intent(context, AccessibilityConsentActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK
                },
            )
            return
        }

        setLaunchingState(context, glanceId, isLaunching = true)

        val result = repository.launchStampApp(context)

        setLaunchingState(context, glanceId, isLaunching = false)

        result.onFailure { throwable ->
            val message =
                when ((throwable as? LaunchException)?.error) {
                    LaunchError.AppNotInstalled ->
                        context.getString(R.string.ERROR_WIDGET_STAMP_APP_NOT_INSTALLED)
                    is LaunchError.LaunchFailed ->
                        context.getString(R.string.ERROR_WIDGET_STAMP_LAUNCH_FAILED)
                    LaunchError.AccessibilityServiceDisabled ->
                        context.getString(R.string.ERROR_WIDGET_ACCESSIBILITY_DISABLED)
                    null -> null
                }
            message?.let { showToast(context, it) }
        }
    }

    private suspend fun setLaunchingState(
        context: Context,
        glanceId: GlanceId,
        isLaunching: Boolean,
    ) {
        updateAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId) { prefs ->
            prefs.toMutablePreferences().apply { this[isLaunchingKey] = isLaunching }
        }
        StampWidget().update(context, glanceId)
    }

    private fun showToast(
        context: Context,
        message: String,
    ) {
        Handler(Looper.getMainLooper()).post {
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }
}
