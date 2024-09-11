package com.punyo.nitechroomvacancyviewer

import android.os.Bundle
import android.webkit.WebView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.punyo.nitechroomvacancyviewer.ui.MainNavigation
import com.punyo.nitechroomvacancyviewer.ui.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AppTheme {
                MainNavigation()
            }
        }
    }
}