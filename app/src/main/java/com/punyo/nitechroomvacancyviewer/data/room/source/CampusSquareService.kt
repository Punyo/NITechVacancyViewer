package com.punyo.nitechroomvacancyviewer.data.room.source

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface CampusSquareService {
    @GET("ssologin.do")
    suspend fun login(
        @Header("User-Agent") agent: String,
        @Header("Cookie") cookies: String,
        @Query("locale") p: String = "ja_JP",
        @Query("page") p2: String = "smart",
    ):Response<Void>
}