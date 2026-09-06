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
import androidx.webkit.WebViewFeature
import org.jsoup.Jsoup

private const val BASE_URL = "https://rpxkyomu.ict.nitech.ac.jp"
private const val TRUSTED_SCHEME = "https"
private const val TRUSTED_HOST = "rpxkyomu.ict.nitech.ac.jp"
private val TRUSTED_ORIGIN_RULES = setOf(BASE_URL)
private const val MAIN_MENU_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussmart.do?page=main"
private const val FLOWEXECUTIONKEY_URL =
    "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussquare.do?_flowId=KHW0001300-flow"

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
                installCampusSquareMessageListeners(onGetReservationTableHTML)
                CookieManager.getInstance().setAcceptCookie(true)
                CookieManager.getInstance().acceptThirdPartyCookies(this)
                CookieManager.getInstance().setCookie(
                    "https://rpxkyomu.ict.nitech.ac.jp/",
                    "sso4cookie=$sso4cookie",
                )
                // authorizationErrorを回避するためにメインページを開く
                loadUrl(MAIN_MENU_URL)
            }
        },
    )
}

private fun WebView.installCampusSquareMessageListeners(onGetReservationTableHTML: (String) -> Unit) {
    // Origin-scoped messagingが利用できない場合も、安全でないinterfaceへはフォールバックしない。
    if (!WebViewFeature.isFeatureSupported(WebViewFeature.WEB_MESSAGE_LISTENER)) return

    // この完全一致HTTPS originにだけJavaScriptオブジェクトを公開する。
    WebViewCompat.addWebMessageListener(this, "callback", TRUSTED_ORIGIN_RULES) {
            _, message, sourceOrigin, isMainFrame, _ ->
        if (!isTrustedCampusSquareMessage(sourceOrigin.scheme, sourceOrigin.host, sourceOrigin.port, isMainFrame)) {
            // 許可originでもsubframeからのメッセージは何も実行せず拒否する。
            return@addWebMessageListener
        }
        message.data?.let(onGetReservationTableHTML)
    }
    WebViewCompat.addWebMessageListener(this, "Extractor", TRUSTED_ORIGIN_RULES) {
            view, message, sourceOrigin, isMainFrame, _ ->
        if (!isTrustedCampusSquareMessage(sourceOrigin.scheme, sourceOrigin.host, sourceOrigin.port, isMainFrame)) {
            return@addWebMessageListener
        }

        val html = message.data ?: return@addWebMessageListener
        val regex = """<a href=["']([^"']+)["']>週表示""".toRegex()
        val matchResult = regex.find(html)
        if (matchResult == null) {
            // 「週表示」のアンカーが見つからない＝もうすでに週表示になっている
            view.loadUrl("javascript:window.callback.postMessage(document.documentElement.outerHTML);")
        } else {
            // 「週表示」のアンカーが見つかった＝週表示に変更する
            val url = BASE_URL + matchResult.groupValues[1]
            view.loadUrl(Jsoup.parse(url).text())
        }
    }
}

internal fun isTrustedCampusSquareMessage(
    scheme: String?,
    host: String?,
    port: Int,
    isMainFrame: Boolean,
): Boolean =
    isMainFrame &&
        scheme == TRUSTED_SCHEME &&
        host == TRUSTED_HOST &&
        (port == -1 || port == 443)

class CampusSquareWebViewClient(
    private val onReceivedError: (WebView?, WebResourceError?) -> Unit,
    private val onReceivedHttpError: (WebView?, WebResourceResponse?) -> Unit,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        // URLにflowExecutionKeyが含まれている=日表示の「施設利用状況参照」ページが開かれている
        // 週表示の「施設利用状況参照」ページへのURLを抽出する
        url?.let {
            if (url == MAIN_MENU_URL) {
                view?.loadUrl(FLOWEXECUTIONKEY_URL)
            }
            if (url.contains("flowExecutionKey")) {
                view?.loadUrl("javascript:window.Extractor.postMessage(document.getElementsByTagName('html')[0].outerHTML);")
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
