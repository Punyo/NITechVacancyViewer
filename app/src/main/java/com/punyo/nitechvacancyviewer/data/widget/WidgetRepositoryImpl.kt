package com.punyo.nitechvacancyviewer.data.widget

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.util.Log
import com.punyo.nitechvacancyviewer.application.service.StampAccessibilityService
import com.punyo.nitechvacancyviewer.data.widget.model.LaunchError
import com.punyo.nitechvacancyviewer.data.widget.model.LaunchException
import com.punyo.nitechvacancyviewer.data.widget.source.AccessibilityChecker
import javax.inject.Inject

/**
 * WidgetRepository の実装クラス。
 * PackageManager と AccessibilityChecker を使用して打刻アプリの起動と状態確認を行う。
 */
class WidgetRepositoryImpl
    @Inject
    constructor(
        private val accessibilityChecker: AccessibilityChecker,
    ) : WidgetRepository {
        companion object {
            private const val STAMP_APP_PACKAGE = "jp.ac.nitech.pyrroline.nativeapp"
            private const val TAG = "WidgetRepository"
        }

        /**
         * 打刻アプリを起動する。
         *
         * Accessibility Service の有効状態を確認し、アプリのインストール確認後に起動する。
         * 各エラーケースで適切な LaunchError を Result.failure にラップして返す。
         */
        override suspend fun launchStampApp(context: Context): Result<Unit> {
            if (!accessibilityChecker.isStampServiceEnabled(context)) {
                Log.w(TAG, "Accessibility Service is disabled")
                return Result.failure(LaunchException(LaunchError.AccessibilityServiceDisabled))
            }

            try {
                @Suppress("DEPRECATION")
                context.packageManager.getPackageInfo(STAMP_APP_PACKAGE, 0)
            } catch (_: PackageManager.NameNotFoundException) {
                Log.w(TAG, "$STAMP_APP_PACKAGE is not installed")
                return Result.failure(LaunchException(LaunchError.AppNotInstalled))
            }

            return try {
                val intent =
                    context.packageManager.getLaunchIntentForPackage(STAMP_APP_PACKAGE)?.apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_REORDER_TO_FRONT
                    } ?: run {
                        Log.e(TAG, "No launch intent found for $STAMP_APP_PACKAGE")
                        return Result.failure(LaunchException(LaunchError.AppNotInstalled))
                    }
                StampAccessibilityService.pendingAutoClick = true
                context.startActivity(intent)
                Log.i(TAG, "$STAMP_APP_PACKAGE launched successfully")
                Result.success(Unit)
            } catch (e: Exception) {
                StampAccessibilityService.pendingAutoClick = false
                Log.e(TAG, "Failed to launch stamp app", e)
                Result.failure(LaunchException(LaunchError.LaunchFailed(e)))
            }
        }

        /**
         * StampAccessibilityService が有効かどうかを確認する。
         *
         * AccessibilityChecker に処理を委譲する。
         */
        override suspend fun isAccessibilityServiceEnabled(context: Context): Boolean =
            accessibilityChecker.isStampServiceEnabled(context)
    }
