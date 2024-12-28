package com.punyo.nitechvacancyviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.punyo.nitechvacancyviewer.ui.MainNavigation

class MainActivity : ComponentActivity() {
    private var showAdinOnResume = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showAdinOnResume = false
        enableEdgeToEdge()
        setContent {
            MainNavigation()
        }
    }
}