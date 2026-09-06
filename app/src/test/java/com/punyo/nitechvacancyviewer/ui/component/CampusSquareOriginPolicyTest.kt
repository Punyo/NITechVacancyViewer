package com.punyo.nitechvacancyviewer.ui.component

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class CampusSquareOriginPolicyTest {
    @Test
    fun `allows trusted HTTPS origin with default or explicit port`() {
        assertTrue(CampusSquareOriginPolicy.isTrusted("https://rpxkyomu.ict.nitech.ac.jp/campusweb/path"))
        assertTrue(CampusSquareOriginPolicy.isTrusted("https://rpxkyomu.ict.nitech.ac.jp:443/redirect"))
    }

    @Test
    fun `rejects untrusted schemes hosts and effective ports`() {
        assertFalse(CampusSquareOriginPolicy.isTrusted("http://rpxkyomu.ict.nitech.ac.jp/campusweb/path"))
        assertFalse(CampusSquareOriginPolicy.isTrusted("https://rpxkyomu.ict.nitech.ac.jp:444/campusweb/path"))
        assertFalse(CampusSquareOriginPolicy.isTrusted("https://rpxkyomu.ict.nitech.ac.jp.evil.example/path"))
        assertFalse(CampusSquareOriginPolicy.isTrusted("https://evil.example/?next=rpxkyomu.ict.nitech.ac.jp"))
    }

    @Test
    fun `rejects missing and malformed URLs`() {
        assertFalse(CampusSquareOriginPolicy.isTrusted(null))
        assertFalse(CampusSquareOriginPolicy.isTrusted("not a URL"))
        assertFalse(CampusSquareOriginPolicy.isTrusted("https:///campusweb/path"))
    }
}
