package com.punyo.nitechvacancyviewer.data.widget

import android.content.Context

/**
 * ウィジェット関連のビジネスロジックを提供する Repository インターフェース。
 * 打刻アプリの起動と Accessibility Service の状態確認を担当する。
 */
interface WidgetRepository {
    /**
     * 打刻アプリを起動する。
     *
     * アプリが未インストールの場合は LaunchException(AppNotInstalled) を含む Result.failure を返す。
     * Accessibility Service が無効の場合は LaunchException(AccessibilityServiceDisabled) を含む Result.failure を返す。
     * 起動に失敗した場合は LaunchException(LaunchFailed) を含む Result.failure を返す。
     *
     * @param context Android Context
     * @return 起動成功時は Result.success(Unit)、失敗時は Result.failure(LaunchException)
     */
    suspend fun launchStampApp(context: Context): Result<Unit>

    /**
     * Accessibility Service（StampAccessibilityService）が有効かどうかを確認する。
     *
     * @param context Android Context
     * @return サービスが有効な場合 true、無効な場合 false
     */
    suspend fun isAccessibilityServiceEnabled(context: Context): Boolean
}
