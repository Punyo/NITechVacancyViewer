package com.punyo.nitechvacancyviewer.ui.component

internal const val WEB_VIEW_LOAD_TIMEOUT_MILLIS = 30_000L

internal fun interface TimeoutCancellation {
    fun cancel()
}

internal fun interface TimeoutScheduler {
    fun schedule(delayMillis: Long, action: () -> Unit): TimeoutCancellation
}

internal enum class WebViewLoadStage {
    MAIN_MENU,
    FLOW_PAGE,
    WEEK_PAGE,
    HTML_EXTRACTION,
}

/** Keeps exactly one timeout associated with the current WebView navigation attempt. */
internal class WebViewLoadTimeoutController(
    private val scheduler: TimeoutScheduler,
    private val timeoutMillis: Long = WEB_VIEW_LOAD_TIMEOUT_MILLIS,
    private val onTimeout: (WebViewLoadStage) -> Unit,
) {
    private var generation = 0L
    private var currentStage: WebViewLoadStage? = null
    private var cancellation: TimeoutCancellation? = null

    val isActive: Boolean
        get() = currentStage != null

    fun start(stage: WebViewLoadStage) {
        cancellation?.cancel()
        currentStage = stage
        val scheduledGeneration = ++generation
        cancellation =
            scheduler.schedule(timeoutMillis) {
                if (scheduledGeneration != generation || currentStage != stage) return@schedule

                cancellation = null
                currentStage = null
                generation++
                onTimeout(stage)
            }
    }

    fun stopAndCreateRetry(action: () -> Unit): () -> Unit {
        val stage = currentStage ?: WebViewLoadStage.MAIN_MENU
        finish()
        return {
            start(stage)
            action()
        }
    }

    fun finish() {
        generation++
        currentStage = null
        cancellation?.cancel()
        cancellation = null
    }
}
