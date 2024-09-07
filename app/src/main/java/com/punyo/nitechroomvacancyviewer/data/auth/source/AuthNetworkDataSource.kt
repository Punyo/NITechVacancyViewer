package com.punyo.nitechroomvacancyviewer.data.auth.source

import android.util.Log
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException
import java.security.AccessControlException
import kotlin.jvm.Throws

object AuthNetworkDataSource {
    private val retrofit = Retrofit.Builder()
        .baseUrl("https://slb4oam.ict.nitech.ac.jp/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val authService = retrofit.create(NITechAuthService::class.java)

    @Throws(AccessControlException::class, IOException::class)
    suspend fun getToken(userName: String, password: String): String? {
        val call = authService.getToken(userName, password)
        Log.d("AuthNetworkDataSource", call.raw().toString())
        if (call.isSuccessful && call.body() != null) {
            return call.body()!!.tokenId
        }
        if (call.code() == 401) {
            throw AccessControlException("Access Denied")
        }
        return null
    }
}