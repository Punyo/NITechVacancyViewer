package com.punyo.nitechvacancyviewer.ui.component

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WebViewLoadTimeoutControllerTest {
    private val scheduler = FakeTimeoutScheduler()
    private val timedOutStages = mutableListOf<WebViewLoadStage>()
    private val controller =
        WebViewLoadTimeoutController(scheduler, 30_000L) {
            timedOutStages += it
        }

    @Test
    fun startingNextStageInvalidatesPreviousTimeout() {
        controller.start(WebViewLoadStage.MAIN_MENU)
        val oldTask = scheduler.tasks.single()

        controller.start(WebViewLoadStage.FLOW_PAGE)
        oldTask.runEvenIfCancelled()

        assertTrue(timedOutStages.isEmpty())
        scheduler.tasks.last().run()
        assertEquals(listOf(WebViewLoadStage.FLOW_PAGE), timedOutStages)
    }

    @Test
    fun finishPreventsLateTimeoutAndDuplicateCallback() {
        controller.start(WebViewLoadStage.HTML_EXTRACTION)
        val task = scheduler.tasks.single()

        controller.finish()
        task.runEvenIfCancelled()
        task.runEvenIfCancelled()

        assertFalse(controller.isActive)
        assertTrue(timedOutStages.isEmpty())
    }

    @Test
    fun retryStartsFreshTimeoutForFailedStage() {
        var retries = 0
        controller.start(WebViewLoadStage.WEEK_PAGE)
        val retry = controller.stopAndCreateRetry { retries++ }

        retry()

        assertEquals(1, retries)
        assertTrue(controller.isActive)
        scheduler.tasks.last().run()
        assertEquals(listOf(WebViewLoadStage.WEEK_PAGE), timedOutStages)
    }
}

private class FakeTimeoutScheduler : TimeoutScheduler {
    val tasks = mutableListOf<FakeTask>()

    override fun schedule(delayMillis: Long, action: () -> Unit): TimeoutCancellation {
        assertEquals(30_000L, delayMillis)
        return FakeTask(action).also(tasks::add)
    }
}

private class FakeTask(private val action: () -> Unit) : TimeoutCancellation {
    private var cancelled = false

    override fun cancel() {
        cancelled = true
    }

    fun run() {
        if (!cancelled) action()
    }

    fun runEvenIfCancelled() = action()
}
