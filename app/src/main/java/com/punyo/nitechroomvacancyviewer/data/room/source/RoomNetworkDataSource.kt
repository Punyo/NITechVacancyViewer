package com.punyo.nitechroomvacancyviewer.data.room.source

import android.app.Application
import retrofit2.Retrofit

object RoomNetworkDataSource {
    private const val USER_AGENT =
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36"
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://rpxkyomu.ict.nitech.ac.jp/campusweb/")
        .build()

    private val campusSquareService = retrofit.create(CampusSquareService::class.java)

    suspend fun login(application: Application, sso4cookie: String): String? {
//        val call = campusSquareService.login(USER_AGENT, getCookieString(sso4cookie, null))
//        if (call.isSuccessful) {
//            Log.d("RoomNetworkDataSource", call.body().toString())
//            return call.headers()["Set-Cookie"]?.let {
//                val cookies = stringToMap(it)
//                cookies["JSESSIONID"]
//            }
//        }
        loadUrlWithCustomHeadersAndInterceptResponse(
            application,
            "https://rpxkyomu.ict.nitech.ac.jp/",
            mapOf("Cookie" to getCookieString(sso4cookie, null))
        )
        return null
    }

    private fun getCookieString(sso4cookie: String, JSESSIONID: String?): String {
        val cookies: MutableMap<String, String> = mutableMapOf(
            "sso4cookie" to sso4cookie,
            "lb4cookie" to "02",
        )
        JSESSIONID?.let { cookies["JSESSIONID"] = it }
        val cookieString = StringBuilder()
        for ((key, value) in cookies) {
            cookieString.append("$key=$value; ")
        }
        return cookieString.toString()
    }

    private fun stringToMap(cookieString: String): Map<String, String> {
        val map = mutableMapOf<String, String>()
        val pairs = cookieString.split("; ")
        for (pair in pairs) {
            val keyValue = pair.split("=")
            if (keyValue.size == 2) {
                map[keyValue[0]] = keyValue[1]
            }
        }
        return map
    }
}