package com.punyo.nitechvacancyviewer.application.service

import android.accessibilityservice.AccessibilityService
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import android.widget.Toast
import com.punyo.nitechvacancyviewer.R

/**
 * 打刻アプリのボタン自動操作を提供する Accessibility Service。
 * 打刻アプリの画面を監視し、「打刻」ボタンを検出して自動クリックする。
 *
 * 3段階のフォールバック戦略でボタンを検出する:
 * 1. View ID による検索（最優先）
 * 2. テキスト「打刻」による検索（フォールバック）
 */
class StampAccessibilityService : AccessibilityService() {
    companion object {
        private const val TAG = "StampAccessibilityService"
        private const val TARGET_PACKAGE = "jp.ac.nitech.pyrroline.nativeapp"
        // 適当に呼び出してたらピロリンの打刻ボタンのIDを引き当ててしまった...
        private const val BUTTON_VIEW_ID = "jp.ac.nitech.pyrroline.nativeapp:id/button_stamp"
        private const val TIMEOUT_MS = 3100L
        private const val RETRY_INTERVAL_MS = 1000L
        private const val MAX_RETRIES = 3

        /**
         * Widget からのタップ操作によって打刻アプリが起動された場合のみ true になるフラグ。
         * WidgetRepositoryImpl が startActivity() 直前に true にセットし、
         * onAccessibilityEvent() で検出を開始する際に false にリセットする。
         * これにより、ユーザーが手動でアプリを起動した場合の誤動作を防ぐ。
         */
        @Volatile
        var pendingAutoClick: Boolean = false
    }

    private var isDetectionInProgress = false
    private var retryCount = 0
    private val handler = Handler(Looper.getMainLooper())

    /**
     * Accessibility Event を受信した際の処理。
     * 打刻アプリのウィンドウ状態変更イベントをフィルタリングし、ボタン検出を開始する。
     */
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        if (event.eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED &&
            event.packageName?.toString() == TARGET_PACKAGE &&
            !isDetectionInProgress &&
            pendingAutoClick
        ) {
            pendingAutoClick = false
            Log.i(TAG, "Target app window detected (launched from widget), starting button detection")
            isDetectionInProgress = true
            retryCount = 0
            startButtonDetection()
        }
    }

    /**
     * サービス中断時の処理。
     */
    override fun onInterrupt() {
        Log.w(TAG, "Accessibility service interrupted")
        cleanupDetection()
    }

    /**
     * ボタン検出を開始する（リトライロジック付き）。
     * Handler.postDelayed() で RETRY_INTERVAL_MS 後に検出を試みる。
     * 最大 MAX_RETRIES 回リトライし、失敗時はタイムアウト処理を実行する。
     */
    private fun startButtonDetection() {
        handler.postDelayed(
            {
                val rootNode = rootInActiveWindow
                val found = rootNode != null && findAndClickStampButton(rootNode)
                @Suppress("DEPRECATION")
                rootNode?.recycle()

                when {
                    found -> {
                        showToast(getString(R.string.WIDGET_STAMP_TOAST_SUCCESS))
                        cleanupDetection()
                    }
                    retryCount < MAX_RETRIES -> {
                        retryCount++
                        Log.i(TAG, "Button detection retry: $retryCount/$MAX_RETRIES")
                        startButtonDetection()
                    }
                    else -> onDetectionTimeout()
                }
            },
            RETRY_INTERVAL_MS,
        )
    }

    /**
     * 「打刻」ボタンを検出してクリックする。
     * 3段階のフォールバック戦略を順番に試みる。
     *
     * @param rootNode ウィンドウのルートノード
     * @return クリック成功の場合 true
     */
    private fun findAndClickStampButton(rootNode: AccessibilityNodeInfo): Boolean {
        // 第1優先: View ID で検索
        rootNode.findAccessibilityNodeInfosByViewId(BUTTON_VIEW_ID)
            .firstOrNull { it.isClickable }
            ?.let {
                return performClickAction(it)
            }

        // 第2優先: テキスト「打刻」で検索し、クリック可能な親ノードを取得
        rootNode.findAccessibilityNodeInfosByText("打刻")
            .firstOrNull()
            ?.parent
            ?.takeIf { it.isClickable }
            ?.let {
                return performClickAction(it)
            }
        Log.w(TAG, "Stamp button not found in current window")
        return false
    }

    /**
     * ノードに対してクリックアクションを実行する。
     *
     * @param node クリック対象のノード
     * @return クリック成功の場合 true
     */
    private fun performClickAction(node: AccessibilityNodeInfo): Boolean {
        val success = node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        if (success) {
            Log.i(TAG, "Button clicked successfully")
        } else {
            Log.e(TAG, "Button found but click action failed")
        }
        @Suppress("DEPRECATION")
        node.recycle()
        return success
    }

    /**
     * タイムアウト時の処理。
     * 打刻ボタンが検出できなかった旨を Toast で通知し、クリーンアップを実行する。
     */
    private fun onDetectionTimeout() {
        Log.w(TAG, "Button detection timeout after ${TIMEOUT_MS}ms")
        showToast(getString(R.string.WIDGET_STAMP_TOAST_BUTTON_NOT_FOUND))
        cleanupDetection()
    }

    /**
     * 検出処理のクリーンアップ。
     * Handler のコールバックを削除し、フラグとカウンタをリセットする。
     */
    private fun cleanupDetection() {
        handler.removeCallbacksAndMessages(null)
        isDetectionInProgress = false
        retryCount = 0
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
