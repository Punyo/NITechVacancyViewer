package com.punyo.nitechvacancyviewer.ui.component

import android.annotation.SuppressLint
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.viewinterop.AndroidView
import androidx.webkit.WebViewCompat
import java.net.URI
import org.jsoup.Jsoup

private const val BASE_URL = "https://rpxkyomu.ict.nitech.ac.jp"
private const val MAIN_MENU_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussmart.do?page=main"
private const val FLOWEXECUTIONKEY_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussquare.do?_flowId=KHW0001300-flow"
private const val CALLBACK_BRIDGE = "callback"
private const val EXTRACTOR_BRIDGE = "Extractor"

internal object CampusSquareOriginPolicy {
    private const val TRUSTED_SCHEME = "https"
    private const val TRUSTED_HOST = "rpxkyomu.ict.nitech.ac.jp"
    private const val TRUSTED_PORT = 443

    val allowedOriginRules: Set<String> = setOf("$TRUSTED_SCHEME://$TRUSTED_HOST")

    fun isTrusted(url: String?): Boolean {
        val uri = runCatching { URI(url ?: return false) }.getOrNull() ?: return false
        val effectivePort = if (uri.port == -1) TRUSTED_PORT else uri.port
        return uri.scheme.equals(TRUSTED_SCHEME, ignoreCase = true) &&
            uri.host.equals(TRUSTED_HOST, ignoreCase = true) &&
            effectivePort == TRUSTED_PORT
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusSquareWebViewComponent(
    onGetReservationTableHTML: (String) -> Unit,
    onReceivedError: (WebView?, WebResourceError?) -> Unit,
    onReceivedHttpError: (WebView?, WebResourceResponse?) -> Unit,
    sso4cookie: String,
) {
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = CampusSquareWebViewClient(onReceivedError, onReceivedHttpError)
                settings.javaScriptEnabled = true
                visibility = View.GONE
                WebViewCompat.addWebMessageListener(
                    this,
                    CALLBACK_BRIDGE,
                    CampusSquareOriginPolicy.allowedOriginRules,
                ) { _, message, sourceOrigin, isMainFrame, _ ->
                    if (isMainFrame && CampusSquareOriginPolicy.isTrusted(sourceOrigin.toString())) {
                        message.data?.let(onGetReservationTableHTML)
                    }
                }
                WebViewCompat.addWebMessageListener(
                    this,
                    EXTRACTOR_BRIDGE,
                    CampusSquareOriginPolicy.allowedOriginRules,
                ) { _, message, sourceOrigin, isMainFrame, _ ->
                    if (!isMainFrame || !CampusSquareOriginPolicy.isTrusted(sourceOrigin.toString())) {
                        return@addWebMessageListener
                    }

                    val html = message.data ?: return@addWebMessageListener
                    val regex = """<a href=["']([^"']+)["']>週表示""".toRegex()
                    val matchResult = regex.find(html)
                    if (matchResult == null) {
                        // 「週表示」のアンカーが見つからない＝もうすでに週表示になっている
                        // HTMLをコールバック
                        post {
                            evaluateJavascript(
                                "window.callback.postMessage(document.documentElement.outerHTML);",
                                null,
                            )
                        }
                    } else {
                        // 「週表示」のアンカーが見つかった＝週表示に変更する
                        val url = BASE_URL + matchResult.groupValues[1]
                        post {
                            loadUrl(Jsoup.parse(url).text())
                        }
                    }
                }
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().acceptThirdPartyCookies(this)
                CookieManager.getInstance().setCookie(
                    "$BASE_URL/",
                    "sso4cookie=$sso4cookie",
                )
                // authorizationErrorを回避するためにメインページを開く
                loadUrl(MAIN_MENU_URL)
            }
        },
    )
}

class CampusSquareWebViewClient(
    private val onReceivedError: (WebView?, WebResourceError?) -> Unit,
    private val onReceivedHttpError: (WebView?, WebResourceResponse?) -> Unit,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        // URLにflowExecutionKeyが含まれている=日表示の「施設利用状況参照」ページが開かれている
        // 週表示の「施設利用状況参照」ページへのURLを抽出する
        url?.let {
            if (!CampusSquareOriginPolicy.isTrusted(url)) return
            if (url == MAIN_MENU_URL) {
                view?.loadUrl(FLOWEXECUTIONKEY_URL)
            }
            if (url.contains("flowExecutionKey")) {
                view?.evaluateJavascript(
                    "window.Extractor.postMessage(document.documentElement.outerHTML);",
                    null,
                )
            }
        }
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest?,
    ): WebResourceResponse? {
        return super.shouldInterceptRequest(view, request)
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        if (request?.url.toString() != MAIN_MENU_URL) {
            onReceivedError(view, error)
        }
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?,
    ) {
        if (request?.url.toString() != MAIN_MENU_URL) {
            onReceivedHttpError(view, errorResponse)
        }
        super.onReceivedHttpError(view, request, errorResponse)
    }
}
