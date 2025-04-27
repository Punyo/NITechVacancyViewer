package com.punyo.nitechvacancyviewer.data.auth

import android.content.Context
import com.punyo.nitechvacancyviewer.data.auth.AuthRepositoryImpl.AuthResultStatus

interface AuthRepository {
    suspend fun signInWithSavedCredentials(context: Context): AuthResultStatus

    suspend fun signIn(
        context: Context,
        userName: String,
        password: String,
    ): AuthResultStatus

    suspend fun signOutAndClearSavedCredentials(context: Context)
}
