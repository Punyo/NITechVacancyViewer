package com.punyo.nitechroomvacancyviewer.data

import android.app.Activity
import android.content.Context
import com.microsoft.identity.client.exception.MsalDeclinedScopeException
import com.microsoft.identity.client.exception.MsalException
import com.microsoft.identity.client.exception.MsalServiceException
import com.microsoft.identity.client.exception.MsalUiRequiredException
import com.microsoft.identity.client.exception.MsalUserCancelException
import com.punyo.nitechroomvacancyviewer.data.source.network.MSGraphNetworkDatasource
import kotlinx.coroutines.CompletableDeferred

class MSGraphRepository {
    val isSignedIn by MSGraphNetworkDatasource::isSignedIn

    suspend fun initMSAL(context: Context): MSALInitResult {
        val deferred = CompletableDeferred<Unit>()
        lateinit var result: MSALInitResult
        MSGraphNetworkDatasource.initialize(context) { exception ->
            result = if (exception != null) {
                MSALInitResult(MSALInitResultStatus.INIT_FAILED, exception)
            } else {
                if (isSignedIn) {
                    MSALInitResult(MSALInitResultStatus.INIT_SUCCESS_AND_SIGNED_IN)
                } else {
                    MSALInitResult(MSALInitResultStatus.INIT_SUCCESS_AND_NOT_SIGNED_IN)
                }
            }
            deferred.complete(Unit)
        }
        deferred.await()
        return result
    }

    fun signIn(activity: Activity, onSignInEnded: (MSALOperationResultStatus) -> Unit) {
        MSGraphNetworkDatasource.signIn(activity, onSignInEnded = { exception ->
            if (exception != null) {
                onSignInEnded(handleMsalException(exception))
            } else {
                if (MSGraphNetworkDatasource.isSignedIn) {
                    onSignInEnded(MSALOperationResultStatus.SUCCESS)
                } else {
                    onSignInEnded(MSALOperationResultStatus.CANCELLED)
                }
            }
        })
    }

    private fun handleMsalException(exception: MsalException): MSALOperationResultStatus {
        return if (exception is MsalUiRequiredException) {
            MSALOperationResultStatus.NEED_RE_SIGN_IN
        } else if (exception is MsalUserCancelException) {
            MSALOperationResultStatus.CANCELLED
        } else if (exception !is MsalServiceException && (exception !is MsalDeclinedScopeException)) {
            MSALOperationResultStatus.NETWORK_ERROR
        } else {
            MSALOperationResultStatus.INTERNAL_ERROR
        }
    }

    data class MSALInitResult(
        val status: MSALInitResultStatus,
        val exception: MsalException? = null
    )

    enum class MSALInitResultStatus {
        INIT_SUCCESS_AND_SIGNED_IN,
        INIT_SUCCESS_AND_NOT_SIGNED_IN,
        INIT_FAILED
    }

    enum class MSALOperationResultStatus {
        SUCCESS,
        NEED_RE_SIGN_IN,
        CANCELLED,
        NETWORK_ERROR,
        INTERNAL_ERROR
    }
}