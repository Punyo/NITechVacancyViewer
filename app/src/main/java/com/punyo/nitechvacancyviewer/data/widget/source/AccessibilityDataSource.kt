package com.punyo.nitechvacancyviewer.data.widget.source

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import javax.inject.Inject

interface AccessibilityChecker {
    /**
     * StampAccessibilityService が有効かどうかを確認する。
     *
     * @param context Android Context
     * @return サービスが有効な場合 true、無効な場合 false
     */
    fun isStampServiceEnabled(context: Context): Boolean
}

class AndroidAccessibilityChecker
@Inject
constructor() : AccessibilityChecker {
    companion object {
        private const val STAMP_SERVICE_CLASS =
            "com.punyo.nitechvacancyviewer.application.service.StampAccessibilityService"
    }

    override fun isStampServiceEnabled(context: Context): Boolean {
        val manager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        val expectedId = "${context.packageName}/$STAMP_SERVICE_CLASS"
        return enabledServices.any { it.id == expectedId }
    }
}
