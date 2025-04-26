package com.punyo.nitechvacancyviewer.application

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class NITechVacancyViewerApplication : Application() {
    override fun onCreate() {
        super.onCreate()
    }
}
