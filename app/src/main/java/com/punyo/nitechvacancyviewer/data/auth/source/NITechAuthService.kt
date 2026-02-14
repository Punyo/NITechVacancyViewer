package com.punyo.nitechvacancyviewer.data.auth.source

import com.punyo.nitechvacancyviewer.data.auth.model.AuthDataModel
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

interface NITechAuthService {
    @POST("openam/json/authenticate")
    suspend fun getToken(
        @Header("X-OpenAM-Username") username: String,
        @Header("X-OpenAM-Password") password: String,
        @Header("X-NoSession") noSession: Boolean = true,
        @Query("authIndexType") authIndexType: String = "service",
        @Query("authIndexValue") authIndexValue: String = "mfaService",
    ): Response<AuthDataModel>
}
