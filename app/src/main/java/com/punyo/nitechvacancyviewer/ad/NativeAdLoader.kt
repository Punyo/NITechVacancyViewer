package com.punyo.nitechvacancyviewer.ad

import android.content.Context
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.resume

object NativeAdLoader {
    private val loadedAds: MutableMap<Long, NativeAd> = mutableMapOf()

    private suspend fun loadAd(context: Context): Long {
        return suspendCancellableCoroutine { continuation ->
            val adLoader = AdLoader.Builder(context, AdConstants.NATIVE_VACANCYCOMPONENT_AD)
                .forNativeAd { nativeAd ->
                    if (continuation.isActive) {
                        val time = Date().time
                        loadedAds[Date().time] = nativeAd
                        continuation.resume(time)
                    }
                }
                .build()
            adLoader.loadAd(AdRequest.Builder().build())
        }
    }

    suspend fun getAd(context: Context): NativeAd {
        return if (loadedAds.isEmpty()) {
            loadedAds[loadAd(context)]!!
        } else {
            val lastLoadTime = loadedAds.keys.first()
            if (Date().time - lastLoadTime > AdConstants.NATIVE_AD_REFRESH_INTERVAL_MILLISECOND) {
                refreshAd(context)
            } else {
                loadedAds[lastLoadTime]!!
            }
        }
    }

    private suspend fun refreshAd(context: Context): NativeAd {
        val lastLoadTime = loadedAds.keys.first()
        val oldAd = loadedAds.remove(lastLoadTime)
        oldAd!!.destroy()
        return loadedAds[loadAd(context)]!!
    }

}