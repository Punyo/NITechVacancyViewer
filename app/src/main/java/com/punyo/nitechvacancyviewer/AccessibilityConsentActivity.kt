package com.punyo.nitechvacancyviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.accessibility.AccessibilityConsentScreen
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AccessibilityConsentActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                AccessibilityConsentScreen()
            }
        }
    }
}