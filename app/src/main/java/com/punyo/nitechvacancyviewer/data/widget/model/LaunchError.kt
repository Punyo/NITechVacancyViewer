package com.punyo.nitechvacancyviewer.data.widget.model

/**
 * 打刻アプリ起動時のエラーケースを表現する sealed class。
 * WidgetRepository.launchStampApp() の失敗時に LaunchException でラップされて返される。
 */
sealed class LaunchError {
    /** 打刻アプリがインストールされていない */
    data object AppNotInstalled : LaunchError()

    /** アプリの起動に失敗した */
    data class LaunchFailed(val exception: Exception) : LaunchError()

    /** Accessibility Service が有効化されていない */
    data object AccessibilityServiceDisabled : LaunchError()
}

/**
 * LaunchError をラップする例外クラス。
 * kotlin.Result<Unit> の失敗ケースで使用する。
 *
 * @param error 起動エラーの詳細
 */
class LaunchException(val error: LaunchError) : Exception(error.toString())
