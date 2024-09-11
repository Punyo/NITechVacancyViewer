package com.punyo.nitechroomvacancyviewer.ui.component

import android.annotation.SuppressLint
import android.webkit.CookieManager
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import com.punyo.nitechroomvacancyviewer.data.auth.AuthRepository
import org.jsoup.Jsoup

private const val BASE_URL = "https://rpxkyomu.ict.nitech.ac.jp"
private const val FLOWEXECUTIONKEY_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussquare.do?_flowId=KHW0001300-flow"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusSquareWebViewComponent(onGetReservationTableHTML: (String) -> Unit) {
    AndroidView(
        factory = ::WebView,
        update = { webView ->
            webView.webViewClient = CampusSquareWebViewClient()
            webView.settings.javaScriptEnabled = true
            webView.visibility = android.view.View.GONE
            webView.addJavascriptInterface(object {
                @android.webkit.JavascriptInterface
                fun callbackHTML(html: String) {
                    onGetReservationTableHTML(html)
                }
            }, "callback")
            webView.addJavascriptInterface(
                object {
                    @android.webkit.JavascriptInterface
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
                "sso4cookie=${AuthRepository.currentToken}"
            )
            webView.loadUrl(FLOWEXECUTIONKEY_URL)
        }
    )
}

class CampusSquareWebViewClient(
) : android.webkit.WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        //FlowExecutionKey取得用のページではなかった場合URLの抽出を試行
        if (url != FLOWEXECUTIONKEY_URL) {
            view?.loadUrl("javascript:window.Extractor.reservationTableDayURLExtractor(document.getElementsByTagName('html')[0].outerHTML);")
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }
}