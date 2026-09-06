package com.punyo.nitechvacancyviewer.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusSquareWebViewComponentTest {
    @Test
    fun `main frame failure is notified`() {
        assertTrue(
            shouldNotifyWebViewError(
                isForMainFrame = true,
                url = "https://rpxkyomu.ict.nitech.ac.jp/campusweb/error",
            ),
        )
    }

    @Test
    fun `subresource failure is not notified`() {
        assertFalse(
            shouldNotifyWebViewError(
                isForMainFrame = false,
                url = "https://rpxkyomu.ict.nitech.ac.jp/campusweb/app.js",
            ),
        )
    }

    @Test
    fun `main menu failure remains ignored for initial navigation`() {
        assertFalse(
            shouldNotifyWebViewError(
                isForMainFrame = true,
                url = "https://rpxkyomu.ict.nitech.ac.jp/campusweb/campussmart.do?page=main",
            ),
        )
    }
}
