package com.punyo.nitechvacancyviewer.ad

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.appopen.AppOpenAd
import java.util.Date

object AppOpenAdLoader {
    private var currentAd: AppOpenAd? = null
    private var adLoadTime: Long? = null
    private var lastAdDismissTime: Long? = null
    private var isLoadingAd = false
    private const val TAG = "AppOpenAdLoader"

    fun showAdIfAvailable(activity: Activity) {
        currentAd?.let { it ->
            if (adLoadTime!! + AdConstants.APPOPEN_AD_VALIDITY_MILLISECOND < Date().time) {
                loadAd(activity)
                return
            }
            lastAdDismissTime?.let { dismissTime ->
                if (dismissTime + AdConstants.APPOPEN_AD_SHOW_INTERVAL_MILLISECOND > Date().time) {
                    return
                }
            }
            it.fullScreenContentCallback = object : FullScreenContentCallback() {
                override fun onAdDismissedFullScreenContent() {
                    lastAdDismissTime = Date().time
                    Log.d(TAG, "AppOpenAd dismissed")
                    loadAd(activity)
                }

                override fun onAdFailedToShowFullScreenContent(adError: AdError) {
                    if (adError.code != AdRequest.ERROR_CODE_NO_FILL) {
                        Log.e(TAG, "AppOpenAd failed to show: $adError")
                    }
                    loadAd(activity)
                }

                override fun onAdShowedFullScreenContent() {
                    Log.d(TAG, "AppOpenAd showed")
                    currentAd = null
                    adLoadTime = null
                }
            }
            it.show(activity)
        }
    }

    fun loadAd(context: Context) {
        if (isLoadingAd) {
            return
        }
        isLoadingAd = true
        val request = AdRequest.Builder().build()
        AppOpenAd.load(
            context,
            AdConstants.APPOPEN_INITIALIZESCREENAD_ID,
            request,
            object : AppOpenAd.AppOpenAdLoadCallback() {
                override fun onAdLoaded(ad: AppOpenAd) {
                    Log.d(TAG, "AppOpenAd loaded")
                    currentAd = ad
                    adLoadTime = Date().time
                    isLoadingAd = false
                }

                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "AppOpenAd failed to load: $loadAdError")
                    isLoadingAd = false
                }
            }
        )
    }

}