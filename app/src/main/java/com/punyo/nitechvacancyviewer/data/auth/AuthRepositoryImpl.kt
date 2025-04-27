package com.punyo.nitechvacancyviewer.data.auth

import android.content.Context
import android.util.Log
import androidx.datastore.core.IOException
import com.punyo.nitechvacancyviewer.data.auth.model.UserCredentialsDataModel
import com.punyo.nitechvacancyviewer.data.auth.source.AuthNetworkDataSource
import com.punyo.nitechvacancyviewer.data.auth.source.UserCredentialsLocalDataSource
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.security.AccessControlException
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val authNetworkDataSource: AuthNetworkDataSource,
        private val userCredentialsLocalDataSource: UserCredentialsLocalDataSource,
    ) : AuthRepository {
        private var currentUserName: String? = null
        private var currentPassword: String? = null
        var currentToken: String? = null
            private set

        override suspend fun signInWithSavedCredentials(context: Context): AuthResultStatus {
            userCredentialsLocalDataSource.loadCredentials(context)?.let {
                return acquireToken(it.userName, it.password)
            } ?: return AuthResultStatus.CREDENTIALS_NOT_FOUND
        }

        override suspend fun signIn(
            context: Context,
            userName: String,
            password: String,
        ): AuthResultStatus {
            val result = acquireToken(userName, password)
            if (result == AuthResultStatus.SUCCESS) {
                userCredentialsLocalDataSource.saveCredentials(
                    context,
                    UserCredentialsDataModel(userName, password),
                )
            }
            return result
        }

        override suspend fun signOutAndClearSavedCredentials(context: Context) {
            userCredentialsLocalDataSource.clearCredentials(context)
            currentToken = null
            currentUserName = null
            currentPassword = null
        }

        private suspend fun acquireToken(
            userName: String,
            password: String,
        ): AuthResultStatus {
            try {
                val token =
                    CoroutineScope(Dispatchers.IO).async {
                        authNetworkDataSource.getToken(
                            userName,
                            password,
                        )
                    }
                val result = token.await()
                if (result != null) {
                    currentToken = result
                    currentPassword = password
                    currentUserName = userName
                    Log.d("AuthRepository", "Token acquired successfully")
                    return AuthResultStatus.SUCCESS
                }
            } catch (e: Exception) {
                Log.e("AuthRepository", e.stackTraceToString())
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

        enum class AuthResultStatus {
            SUCCESS,
            INVALID_CREDENTIALS,
            NETWORK_ERROR,
            CREDENTIALS_NOT_FOUND,
            UNKNOWN_ERROR,
        }
    }
