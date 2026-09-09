package com.punyo.nitechvacancyviewer.ui.component

import android.annotation.SuppressLint
import android.os.Handler
import android.os.Looper
import android.view.View
import android.webkit.CookieManager
import android.webkit.JavascriptInterface
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.viewinterop.AndroidView
import org.jsoup.Jsoup

private const val BASE_URL = "https://rpxkyomu.ict.nitech.ac.jp"
private const val MAIN_MENU_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussmart.do?page=main"
private const val FLOWEXECUTIONKEY_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussquare.do?_flowId=KHW0001300-flow"

@SuppressLint("SetJavaScriptEnabled")
@Composable
fun CampusSquareWebViewComponent(
    onGetReservationTableHTML: (String) -> Unit,
    onReceivedError: (retry: () -> Unit, WebResourceError?) -> Unit,
    onReceivedHttpError: (WebView?, WebResourceResponse?) -> Unit,
    onLoadTimeout: (retry: () -> Unit) -> Unit,
    sso4cookie: String,
) {
    val currentOnTimeout = rememberUpdatedState(onLoadTimeout)
    var webView: WebView? = null
    lateinit var timeoutController: WebViewLoadTimeoutController
    timeoutController =
        remember {
            val handler = Handler(Looper.getMainLooper())
            WebViewLoadTimeoutController(
                scheduler = { delayMillis, action ->
                    val runnable = Runnable(action)
                    handler.postDelayed(runnable, delayMillis)
                    TimeoutCancellation { handler.removeCallbacks(runnable) }
                },
                onTimeout = { stage ->
                    currentOnTimeout.value {
                        timeoutController.start(stage)
                        webView?.reload()
                    }
                },
            )
        }
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webView = this
                webViewClient =
                    CampusSquareWebViewClient(
                        timeoutController = timeoutController,
                        onReceivedError = onReceivedError,
                        onReceivedHttpError = onReceivedHttpError,
                    )
                settings.javaScriptEnabled = true
                visibility = View.GONE
                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun callbackHTML(html: String) {
                            post {
                                if (!timeoutController.isActive) return@post
                                timeoutController.finish()
                                onGetReservationTableHTML(html)
                            }
                        }
                    },
                    "callback",
                )
                addJavascriptInterface(
                    object {
                        @JavascriptInterface
                        fun reservationTableDayURLExtractor(html: String) {
                            val regex = """<a href=["']([^"']+)["']>週表示""".toRegex()
                            val matchResult = regex.find(html)
                            if (matchResult == null) {
                                // 「週表示」のアンカーが見つからない＝もうすでに週表示になっている
                                // HTMLをコールバック
                                post {
                                    if (!timeoutController.isActive) return@post
                                    loadUrl("javascript:window.callback.callbackHTML(document.documentElement.outerHTML);")
                                }
                            } else {
                                // 「週表示」のアンカーが見つかった＝週表示に変更する
                                val url = BASE_URL + matchResult.groupValues[1]
                                post {
                                    if (!timeoutController.isActive) return@post
                                    timeoutController.start(WebViewLoadStage.WEEK_PAGE)
                                    loadUrl(Jsoup.parse(url).text())
                                }
                            }
                        }
                    },
                    "Extractor",
                )
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().setCookie(
                    "https://rpxkyomu.ict.nitech.ac.jp/",
                    "sso4cookie=$sso4cookie",
                )
                // authorizationErrorを回避するためにメインページを開く
                timeoutController.start(WebViewLoadStage.MAIN_MENU)
                loadUrl(MAIN_MENU_URL)
            }
        },
        onRelease = {
            timeoutController.finish()
            webView = null
            it.stopLoading()
            it.destroy()
        },
    )
}

internal class CampusSquareWebViewClient(
    private val timeoutController: WebViewLoadTimeoutController,
    private val onReceivedError: (retry: () -> Unit, WebResourceError?) -> Unit,
    private val onReceivedHttpError: (WebView?, WebResourceResponse?) -> Unit,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        if (!timeoutController.isActive) return
        // URLにflowExecutionKeyが含まれている=日表示の「施設利用状況参照」ページが開かれている
        // 週表示の「施設利用状況参照」ページへのURLを抽出する
        url?.let {
            if (url == MAIN_MENU_URL) {
                timeoutController.start(WebViewLoadStage.FLOW_PAGE)
                view?.loadUrl(FLOWEXECUTIONKEY_URL)
            }
            if (url.contains("flowExecutionKey")) {
                timeoutController.start(WebViewLoadStage.HTML_EXTRACTION)
                view?.loadUrl("javascript:window.Extractor.reservationTableDayURLExtractor(document.getElementsByTagName('html')[0].outerHTML);")
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
        if (request?.isForMainFrame == true && timeoutController.isActive) {
            val retry = timeoutController.stopAndCreateRetry { view?.reload() }
            onReceivedError(retry, error)
        }
        super.onReceivedError(view, request, error)
    }

    override fun onReceivedHttpError(
        view: WebView?,
        request: WebResourceRequest?,
        errorResponse: WebResourceResponse?,
    ) {
        if (request?.isForMainFrame == true && timeoutController.isActive) {
            timeoutController.finish()
            onReceivedHttpError(view, errorResponse)
        }
        super.onReceivedHttpError(view, request, errorResponse)
    }
}
