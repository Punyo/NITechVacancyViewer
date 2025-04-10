package com.punyo.nitechvacancyviewer.data.auth.source

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.AccessControlException
import java.time.Duration
import kotlin.jvm.Throws

object AuthNetworkDataSource {
    private val retrofit =
        Retrofit
            .Builder()
            .baseUrl("https://slb4oam.ict.nitech.ac.jp/")
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient
                    .Builder()
                    .writeTimeout(Duration.ZERO)
                    .readTimeout(Duration.ZERO)
                    .build(),
            ).build()

    private val authService = retrofit.create(NITechAuthService::class.java)

    @Throws(AccessControlException::class, IOException::class)
    suspend fun getToken(
        userName: String,
        password: String,
    ): String? {
        val call = authService.getToken(userName, password)
        if (call.isSuccessful && call.body() != null) {
            return call.body()!!.tokenId
        }
        if (call.code() == 401) {
            throw AccessControlException("Access Denied")
        }
        return null
    }
}
