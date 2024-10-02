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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MobileAds.initialize(this)
        AppOpenAdLoader.loadAdAndCache(this)
        NativeAdLoader.loadAdAndCache(
            this,
            AdConstants.NATIVE_AD_INITIAL_CACHE_AMOUNT,
            AdConstants.NATIVE_VACANCYCOMPONENT_AD
        )
        enableEdgeToEdge()
        setContent {
            MainNavigation()
        }
    }

    override fun onResume() {
        super.onResume()
        AppOpenAdLoader.showAdIfAvailable(this)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}