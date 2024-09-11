package com.punyo.nitechroomvacancyviewer.data.room.source
import android.annotation.SuppressLint
import android.app.Application

@SuppressLint("SetJavaScriptEnabled")
 fun loadUrlWithCustomHeadersAndInterceptResponse(
    context: Application,
    url: String,
    requestHeaders: Map<String, String>
){
//    val webView = WebView(context)
//    webView.settings.javaScriptEnabled = true
//    val cookieManager = CookieManager.getInstance()
//    cookieManager.setAcceptCookie(true)
//    cookieManager.setAcceptThirdPartyCookies(webView, true)
//    cookieManager.setCookie("https://rpxkyomu.ict.nitech.ac.jp/", "sso4cookie=${AuthRepository.currentToken}")
//    webView.loadUrl(url)
}