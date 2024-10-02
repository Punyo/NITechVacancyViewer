package com.punyo.nitechvacancyviewer.ad

import com.punyo.nitechvacancyviewer.BuildConfig

object AdConstants {
    val APPOPEN_INITIALIZESCREENAD_ID =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/9257395921"
        else "ca-app-pub-6759533750115056/5528687173"
    val NATIVE_VACANCYCOMPONENT_AD =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110"
        else "ca-app-pub-6759533750115056/4819613883"
    val NATIVE_ROOMVACANCYSCREEN_AD =
        if (BuildConfig.DEBUG) "ca-app-pub-3940256099942544/2247696110"
        else "ca-app-pub-6759533750115056/7931860366"
    const val APPOPEN_AD_VALIDITY_MILLISECOND = 3600000L * 4
    const val APPOPEN_AD_SHOW_INTERVAL_MILLISECOND = 1000L
    const val NATIVE_AD_REFRESH_INTERVAL_MILLISECOND = 60000L
    const val NATIVE_AD_INITIAL_CACHE_AMOUNT = 2
}