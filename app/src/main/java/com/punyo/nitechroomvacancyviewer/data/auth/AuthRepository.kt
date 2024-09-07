package com.punyo.nitechroomvacancyviewer.data.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import com.punyo.nitechroomvacancyviewer.data.auth.model.UserCredentialsDataModel
import com.punyo.nitechroomvacancyviewer.data.auth.source.AuthNetworkDataSource
import com.punyo.nitechroomvacancyviewer.data.auth.source.UserCredentialsLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.security.AccessControlException
import kotlinx.coroutines.async

object AuthRepository {
    private var currentUserName: String? = null
    private var currentPassword: String? = null
    private var currentToken: String? = null

    private fun setCurrentUser(userName: String, password: String) {
        currentUserName = userName
        currentPassword = password
    }

    suspend fun signInWithSavedCredentials(context: Context): AuthResultStatus {
        UserCredentialsLocalDataSource.loadCredentials(context)?.let {
            return acquireToken(it.userName, it.password)
        } ?: return AuthResultStatus.CREDENTIALS_NOT_FOUND
    }

    suspend fun signIn(context: Context, userName: String, password: String): AuthResultStatus {
        val result = acquireToken(userName, password)
        if (result == AuthResultStatus.SUCCESS) {
            UserCredentialsLocalDataSource.saveCredentials(
                context,
                UserCredentialsDataModel(userName, password)
            )
        }
        return result
    }

    private suspend fun acquireToken(userName: String, password: String): AuthResultStatus {
        try {
            val token =
                CoroutineScope(Dispatchers.Main).async {
                    AuthNetworkDataSource.getToken(
                        userName,
                        password
                    )
                }
            val result = token.await()
            if (result != null) {
                currentToken = result
                setCurrentUser(userName, password)
                Log.d("AuthRepository", "Token acquired successfully")
                return AuthResultStatus.SUCCESS
            }
        } catch (e: Exception) {
            when (e) {
                is AccessControlException -> return AuthResultStatus.INVALID_CREDENTIALS
                is IOException -> return AuthResultStatus.NETWORK_ERROR
                else -> {
                    Log.e("AuthRepository", e.stackTraceToString())
                    return AuthResultStatus.UNKNOWN_ERROR
                }
            }
        }
        return AuthResultStatus.UNKNOWN_ERROR
    }

    suspend fun refreshToken(): AuthResultStatus {
        currentUserName?.let { userName ->
            currentPassword?.let { password ->
                return acquireToken(userName, password)
            }
        }
        return AuthResultStatus.CREDENTIALS_NOT_FOUND
    }

    enum class AuthResultStatus {
        SUCCESS,
        INVALID_CREDENTIALS,
        NETWORK_ERROR,
        CREDENTIALS_NOT_FOUND,
        UNKNOWN_ERROR
    }
}