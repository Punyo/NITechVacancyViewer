package com.punyo.nitechvacancyviewer.ui.component

import android.annotation.SuppressLint
import android.util.Log
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import org.jsoup.Jsoup

private const val BASE_URL = "https://rpxkyomu.ict.nitech.ac.jp"
private const val MAIN_MENU_URL = "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussmart.do?page=main"
private const val FLOWEXECUTIONKEY_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussquare.do?_flowId=KHW0001300-flow"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusSquareWebViewComponent(onGetReservationTableHTML: (String) -> Unit, sso4cookie: String) {
    AndroidView(
        factory = ::WebView,
        update = { webView ->
            webView.webViewClient = CampusSquareWebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.visibility = android.view.View.GONE
            webView.addJavascriptInterface(object {
                @JavascriptInterface
                fun callbackHTML(html: String) {
                    onGetReservationTableHTML(html)
                }
            }, "callback")
            webView.addJavascriptInterface(
                object {
                    @JavascriptInterface
                    fun reservationTableDayURLExtractor(html: String) {
                        val regex = """<a href=["']([^"']+)["']>週表示""".toRegex()
                        val matchResult = regex.find(html)
                        if (matchResult == null) {
                            //「週表示」のアンカーが見つからない＝もうすでに週表示になっている
                            //HTMLをコールバック
                            webView.post {
                                webView.loadUrl("javascript:window.callback.callbackHTML(document.getElementsByTagName('html')[0].outerHTML);")
                            }
                        } else {
                            //「週表示」のアンカーが見つかった＝週表示に変更する
                            val url = BASE_URL + matchResult.groupValues[1]
                            webView.post {
                                webView.loadUrl(Jsoup.parse(url).text())
                            }
                        }
                    }
                },
                "Extractor"
            )
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(webView, true)
            CookieManager.getInstance().setCookie(
                "https://rpxkyomu.ict.nitech.ac.jp/",
                "sso4cookie=${sso4cookie}"
            )
            //authorizationErrorを回避するためにメインページを開く
            webView.loadUrl(MAIN_MENU_URL)
        }
    )
}

class CampusSquareWebViewClient(
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        //URLにflowExecutionKeyが含まれている=日表示の「施設利用状況参照」ページが開かれている
        //週表示の「施設利用状況参照」ページへのURLを抽出する
        url?.let {
            Log.d("CampusSquareWebViewClient", "$url loaded on CampusSquareWebViewComponent")
            if(url==MAIN_MENU_URL){
                view?.loadUrl(FLOWEXECUTIONKEY_URL)
            }
            if (url.contains("flowExecutionKey")) {
                view?.loadUrl("javascript:window.Extractor.reservationTableDayURLExtractor(document.getElementsByTagName('html')[0].outerHTML);")
            }
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        Log.d("CampusSquareWebViewClient", "shouldInterceptRequest: ${request?.url}")
        return super.shouldInterceptRequest(view, request)
    }
}