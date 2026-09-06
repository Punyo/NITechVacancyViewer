package com.punyo.nitechvacancyviewer.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusSquareWebViewComponentTest {
    @Test
    fun `trusted HTTPS origin in main frame is accepted`() {
        assertTrue(isTrustedCampusSquareMessage("https", "rpxkyomu.ict.nitech.ac.jp", -1, true))
        assertTrue(isTrustedCampusSquareMessage("https", "rpxkyomu.ict.nitech.ac.jp", 443, true))
    }

    @Test
    fun `untrusted schemes hosts and ports are rejected`() {
        assertFalse(isTrustedCampusSquareMessage("http", "rpxkyomu.ict.nitech.ac.jp", -1, true))
        assertFalse(isTrustedCampusSquareMessage("https", "evilrpxkyomu.ict.nitech.ac.jp", -1, true))
        assertFalse(isTrustedCampusSquareMessage("https", "rpxkyomu.ict.nitech.ac.jp.evil.example", -1, true))
        assertFalse(isTrustedCampusSquareMessage("https", "rpxkyomu.ict.nitech.ac.jp", 8443, true))
    }

    @Test
    fun `subframe is rejected even when its origin is trusted`() {
        assertFalse(isTrustedCampusSquareMessage("https", "rpxkyomu.ict.nitech.ac.jp", -1, false))
    }
}
