package com.punyo.nitechvacancyviewer.data.widget.source

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.Context
import android.view.accessibility.AccessibilityManager
import com.punyo.nitechvacancyviewer.application.service.StampAccessibilityService
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

class AccessibilityDataSource
@Inject
constructor() : AccessibilityChecker {
    override fun isStampServiceEnabled(context: Context): Boolean {
        val manager =
            context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
        val enabledServices =
            manager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
        return enabledServices.any {
            it.resolveInfo.serviceInfo.name?.let { serviceClassName ->
                serviceClassName == StampAccessibilityService::class.java.canonicalName
            } ?: false
        }
    }
}
