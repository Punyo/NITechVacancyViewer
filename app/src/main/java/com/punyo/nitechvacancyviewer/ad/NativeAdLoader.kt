package com.punyo.nitechvacancyviewer.ad

import android.content.Context
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.nativead.NativeAd
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.Date
import kotlin.coroutines.resume

object NativeAdLoader {
    private val cachedAds: MutableMap<String, MutableList<NativeAdWithConsumeTime>> = mutableMapOf()
    private val currentAds: MutableMap<String, NativeAdWithConsumeTime> = mutableMapOf()

    private suspend fun loadAd(
        context: Context,
        adUnitId: String,
        amount: Int
    ): List<NativeAdWithConsumeTime> {
        return suspendCancellableCoroutine { continuation ->
            var loadedAdNum = 0
            val loadedAds = mutableListOf<NativeAdWithConsumeTime>()
            val adLoader = AdLoader.Builder(context, adUnitId)
                .forNativeAd { nativeAd ->
                    if (continuation.isActive) {
                        val time = Date().time
                        loadedAdNum++
                        loadedAds.add(NativeAdWithConsumeTime(nativeAd, time))
                        if (loadedAdNum == amount) {
                            continuation.resume(loadedAds)
                        }
                    }
                }
                .build()
            adLoader.loadAds(AdRequest.Builder().build(), amount)
        }
    }

    fun loadAdAndCache(context: Context, amount: Int, adUnitId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            if (cachedAds[adUnitId] == null) {
                cachedAds[adUnitId] = mutableListOf()
            }
            val cachedAdForCurrentId = cachedAds[adUnitId] ?: mutableListOf()
            if (amount + cachedAdForCurrentId.size <= AdConstants.NATIVE_AD_MAX_CACHE_AMOUNT) {
                cachedAdForCurrentId.addAll(loadAd(context, adUnitId, amount))
            } else {
                val cacheableAmount =
                    AdConstants.NATIVE_AD_MAX_CACHE_AMOUNT - cachedAdForCurrentId.size
                if (cacheableAmount > 0) {
                    cachedAdForCurrentId.addAll(
                        loadAd(context, adUnitId, cacheableAmount)
                    )
                }
            }
        }
    }

    suspend fun getAd(context: Context, adUnitId: String): NativeAd {
        val currentAd = currentAds[adUnitId]
        if (currentAd != null) {
            return if (currentAd.consumeTime!! + AdConstants.NATIVE_AD_REFRESH_INTERVAL_MILLISECOND < Date().time) {
                refreshAd(context, adUnitId)
            } else {
                currentAd.nativeAd
            }
        } else {
            val cachedAd = cachedAds[adUnitId]!!.removeFirstOrNull()
            if (cachedAd != null) {
                currentAds[adUnitId] = cachedAd.copy(consumeTime = Date().time)
                return cachedAd.nativeAd
            } else {
                loadAd(context, adUnitId, 1).firstOrNull()?.let {
                    currentAds[adUnitId] = it.copy(consumeTime = Date().time)
                    return it.nativeAd
                }
            }
        }
        return loadAd(context, adUnitId, 1).first().nativeAd
    }

    private suspend fun refreshAd(context: Context, adUnitId: String): NativeAd {
        val currentAd = currentAds[adUnitId]
        if (currentAd != null) {
            currentAds.remove(adUnitId)!!.nativeAd.destroy()
            return getAd(context, adUnitId)
        } else {
            throw NullPointerException("No ad to refresh")
        }
    }
}

private data class NativeAdWithConsumeTime(
    val nativeAd: NativeAd,
    val consumeTime: Long? = null
)