package com.punyo.nitechroomvacancyviewer.data.auth.source

import com.punyo.nitechroomvacancyviewer.data.auth.model.AuthDataModel
import retrofit2.Response
import retrofit2.http.Header
import retrofit2.http.POST

interface NITechAuthService {
    @POST("openam/json/authenticate")
    suspend fun getToken(
        @Header("X-OpenAM-Username") username: String,
        @Header("X-OpenAM-Password") password: String,
        @Header("X-NoSession") noSession: Boolean = true
    ): Response<AuthDataModel>
}