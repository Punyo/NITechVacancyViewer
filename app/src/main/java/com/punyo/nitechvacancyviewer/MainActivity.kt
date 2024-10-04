package com.punyo.nitechvacancyviewer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.android.gms.ads.MobileAds
import com.punyo.nitechvacancyviewer.ad.AdConstants
import com.punyo.nitechvacancyviewer.ad.AppOpenAdLoader
import com.punyo.nitechvacancyviewer.ad.NativeAdLoader
import com.punyo.nitechvacancyviewer.ui.MainNavigation

class MainActivity : ComponentActivity() {
    private var showAdinOnResume = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        showAdinOnResume = false
        MobileAds.initialize(this)
        AppOpenAdLoader.loadAdAndCache(this)
        NativeAdLoader.loadAdAndCache(
            this,
            AdConstants.NATIVE_AD_INITIAL_CACHE_AMOUNT,
            AdConstants.NATIVE_VACANCYCOMPONENT_AD
        )
        NativeAdLoader.loadAdAndCache(
            this,
            AdConstants.NATIVE_AD_INITIAL_CACHE_AMOUNT,
            AdConstants.NATIVE_ROOMVACANCYSCREEN_AD
        )
        enableEdgeToEdge()
        setContent {
            MainNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        if (showAdinOnResume) {
            AppOpenAdLoader.showAdIfAvailable(this)
        } else {
            showAdinOnResume = true
        }
    }
}