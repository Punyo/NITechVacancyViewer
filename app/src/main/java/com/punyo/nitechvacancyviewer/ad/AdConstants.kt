package com.punyo.nitechvacancyviewer.ad

import com.punyo.nitechvacancyviewer.BuildConfig

object AdConstants {
    val APPOPEN_INITIALIZESCREENAD_ID =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/9257395921"
        else "ca-app-pub-6759533750115056/5528687173"
    const val APPOPEN_AD_VALIDITY_MILLISECOND = 3600000L * 4
    const val APPOPEN_AD_SHOW_INTERVAL_MILLISECOND = 10000L
}