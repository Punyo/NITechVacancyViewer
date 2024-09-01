package com.punyo.nitechroomvacancyviewer.data.source.network

import android.app.Activity
import android.content.Context
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAccount
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.IPublicClientApplication
import com.microsoft.identity.client.ISingleAccountPublicClientApplication
import com.microsoft.identity.client.PublicClientApplication
import com.microsoft.identity.client.SignInParameters
import com.microsoft.identity.client.exception.MsalException
import com.punyo.nitechroomvacancyviewer.R

object MSGraphNetworkDatasource {
    private var singleApp: ISingleAccountPublicClientApplication? = null
    private var account: IAccount? = null
    private const val LOGIN_HINT = "@ict.nitech.ac.jp"
    val isSignedIn get() = singleApp != null && account != null

    fun initialize(context: Context, onInitEnd: ((MsalException?) -> Unit)) {
        PublicClientApplication.createSingleAccountPublicClientApplication(context,
            R.raw.auth_config_single_account,
            object : IPublicClientApplication.ISingleAccountApplicationCreatedListener {
                override fun onCreated(application: ISingleAccountPublicClientApplication) {
                    singleApp = application
                    application.getCurrentAccountAsync(object :
                        ISingleAccountPublicClientApplication.CurrentAccountCallback {
                        override fun onAccountLoaded(activeAccount: IAccount?) {
                            account = activeAccount
                            onInitEnd(null)
                        }

                        override fun onAccountChanged(
                            priorAccount: IAccount?,
                            currentAccount: IAccount?
                        ) {
                            onInitEnd(null)
                        }

                        override fun onError(exception: MsalException) {
                            onInitEnd(exception)
                        }
                    })
                }

                override fun onError(exception: MsalException) {
                    onInitEnd(exception)
                }
            })
    }

    fun signIn(activity: Activity, onSignInEnded: (MsalException?) -> Unit) {
        val signInParameters: SignInParameters = SignInParameters.builder()
            .withActivity(activity)
            .withScope("https://graph.microsoft.com/User.Read")
            .withLoginHint(LOGIN_HINT)
            .withCallback(authCallback(onSignInEnded))
            .build()
         singleApp!!.signIn(signInParameters)
    }

    private fun authCallback(onAuthEnded: (MsalException?) -> Unit): AuthenticationCallback {
        return object : AuthenticationCallback {
            override fun onSuccess(authenticationResult: IAuthenticationResult?) {
                account = authenticationResult!!.account
                onAuthEnded(null)
            }

            override fun onError(exception: MsalException?) {
                onAuthEnded(exception)
            }

            override fun onCancel() {
                onAuthEnded(null)
            }
        }
    }
}