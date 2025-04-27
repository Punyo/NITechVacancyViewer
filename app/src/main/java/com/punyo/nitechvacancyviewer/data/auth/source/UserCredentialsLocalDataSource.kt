package com.punyo.nitechvacancyviewer.data.auth.source

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.punyo.nitechvacancyviewer.data.auth.model.UserCredentialsDataModel
import kotlinx.coroutines.flow.first
import java.lang.Exception
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class UserCredentialsLocalDataSource {
    companion object {
        private const val KEYSTORE_ALIAS = "UserCredentials"
        private const val KEYSET_NAME = "credentials_keyset"
        private const val PREF_FILE_NAME = "credentials_preference"
    }

    private val usernamePreference = stringPreferencesKey("username")
    private val passwordPREFERENCE = stringPreferencesKey("password")
    private val Context.dataStore by preferencesDataStore(name = "credentials")
    private lateinit var aead: Aead

    suspend fun loadCredentials(context: Context): UserCredentialsDataModel? {
        try {
            val dataStore: DataStore<Preferences> = context.dataStore
            val userName = dataStore.data.first()[usernamePreference]
            val password = dataStore.data.first()[passwordPREFERENCE]
            return if (userName != null && password != null) {
                decryptCredentials(context, UserCredentialsDataModel(userName, password))
            } else {
                null
            }
        } catch (e: NoSuchElementException) {
            return null
        } catch (e: Exception) {
            Log.e(
                "UserCredentialsLocalDataSource",
                "Loading credentials failed: ${e.message}\nClear existing credentials...",
            )
            clearCredentials(context)
            return null
        }
    }

    suspend fun saveCredentials(
        context: Context,
        userCredentials: UserCredentialsDataModel,
    ) {
        val dataStore: DataStore<Preferences> = context.dataStore
        val encryptedCredentials = encryptCredentials(context, userCredentials)
        dataStore.edit { preferences ->
            preferences[usernamePreference] = encryptedCredentials.userName
            preferences[passwordPREFERENCE] = encryptedCredentials.password
        }
    }

    suspend fun clearCredentials(context: Context) {
        val dataStore: DataStore<Preferences> = context.dataStore
        dataStore.edit { preferences ->
            preferences.remove(usernamePreference)
            preferences.remove(passwordPREFERENCE)
        }
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encryptCredentials(
        context: Context,
        credential: UserCredentialsDataModel,
    ): UserCredentialsDataModel {
        if (::aead.isInitialized.not()) {
            initializeAead(context)
        }
        val encryptedUserName = aead.encrypt(credential.userName.toByteArray(), null)
        val encryptedPassword = aead.encrypt(credential.password.toByteArray(), null)
        return UserCredentialsDataModel(
            Base64.encode(encryptedUserName),
            Base64.encode(encryptedPassword),
        )
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun decryptCredentials(
        context: Context,
        credential: UserCredentialsDataModel,
    ): UserCredentialsDataModel {
        if (::aead.isInitialized.not()) {
            initializeAead(context)
        }
        val decryptedUserName = aead.decrypt(Base64.decode(credential.userName), null)
        val decryptedPassword = aead.decrypt(Base64.decode(credential.password), null)
        return UserCredentialsDataModel(
            decryptedUserName.toString(Charsets.UTF_8),
            decryptedPassword.toString(Charsets.UTF_8),
        )
    }

    private fun initializeAead(context: Context) {
        AeadConfig.register()
        aead =
            AndroidKeysetManager
                .Builder()
                .withSharedPref(
                    context,
                    KEYSET_NAME,
                    PREF_FILE_NAME,
                ).withKeyTemplate(KeyTemplates.get("AES256_GCM"))
                .withMasterKeyUri("android-keystore://$KEYSTORE_ALIAS")
                .build()
                .keysetHandle
                .getPrimitive(Aead::class.java)
    }
}
