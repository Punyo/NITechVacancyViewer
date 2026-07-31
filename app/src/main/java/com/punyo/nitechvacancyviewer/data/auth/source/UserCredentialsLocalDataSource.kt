package com.punyo.nitechvacancyviewer.data.auth.source

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.google.crypto.tink.Aead
import com.google.crypto.tink.KeyTemplates
import com.google.crypto.tink.aead.AeadConfig
import com.google.crypto.tink.integration.android.AndroidKeysetManager
import com.google.crypto.tink.integration.android.AndroidKeystoreKmsClient
import com.punyo.nitechvacancyviewer.data.auth.model.UserCredentialsDataModel
import kotlinx.coroutines.flow.first
import java.io.IOException
import java.lang.Exception
import java.security.GeneralSecurityException
import java.security.ProviderException
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

class UserCredentialsLocalDataSource {
    companion object {
        internal const val KEYSTORE_ALIAS = "UserCredentials"
        internal const val PREF_FILE_NAME = "credentials_preference"
        internal const val DATASTORE_NAME = "credentials"
        private const val KEYSET_NAME = "credentials_keyset"
        private const val MASTER_KEY_URI = "android-keystore://$KEYSTORE_ALIAS"
    }

    private val usernamePreference = stringPreferencesKey("username")
    private val passwordPreference = stringPreferencesKey("password")
    private val Context.dataStore by preferencesDataStore(name = DATASTORE_NAME)
    private var aead: Aead? = null

    suspend fun loadCredentials(context: Context): UserCredentialsDataModel? {
        val dataStore: DataStore<Preferences> = context.dataStore
        val storedCredentials =
            try {
                val userName = dataStore.data.first()[usernamePreference]
                val password = dataStore.data.first()[passwordPreference]
                if (userName == null || password == null) return null
                UserCredentialsDataModel(userName, password)
            } catch (_: NoSuchElementException) {
                return null
            }
        return try {
            decryptCredentials(context, storedCredentials)
        } catch (e: Exception) {
            when {
                isRecoverableKeysetFailure(e) -> {
                    resetKeyset(context)
                    null
                }

                else -> throw e
            }
        }
    }

    suspend fun saveCredentials(
        context: Context,
        userCredentials: UserCredentialsDataModel,
    ) {
        val dataStore: DataStore<Preferences> = context.dataStore
        val encryptedCredentials =
            try {
                encryptCredentials(context, userCredentials)
            } catch (e: Exception) {
                when {
                    isRecoverableKeysetFailure(e) -> {
                        // keyset が無い状態から作り直せば master key ごと再生成されるため、1 度だけ再試行する。
                        resetKeyset(context)
                        encryptCredentials(context, userCredentials)
                    }

                    else -> throw e
                }
            }
        dataStore.edit { preferences ->
            preferences[usernamePreference] = encryptedCredentials.userName
            preferences[passwordPreference] = encryptedCredentials.password
        }
    }

    suspend fun clearCredentials(context: Context) {
        val dataStore: DataStore<Preferences> = context.dataStore
        dataStore.edit { preferences ->
            preferences.remove(usernamePreference)
            preferences.remove(passwordPreference)
        }
    }

    /**
     * keysetを破棄して初期状態へ戻せば回復できる失敗かどうかを判定する
     */
    private fun isRecoverableKeysetFailure(e: Exception): Boolean =
        e is GeneralSecurityException || e is ProviderException || e is IOException

    /**
     * Tink の keyset と Android Keystore の master key を破棄し、初期状態へ戻す。
     *
     * AndroidKeysetManagerはkeysetが存在する経路ではmaster keyを再生成しないため、
     * 端末移行等でmaster keyを失った場合はkeysetごと削除して作り直す
     * */
    @Suppress("ApplySharedPref")
    private suspend fun resetKeyset(context: Context) {
        aead = null
        // apply()では削除が非同期になるのでcommit()で同期的に削除する
        context
            .getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE)
            .edit()
            .clear()
            .commit()
        AndroidKeystoreKmsClient().deleteKey(MASTER_KEY_URI)
        clearCredentials(context)
    }

    @OptIn(ExperimentalEncodingApi::class)
    private fun encryptCredentials(
        context: Context,
        credential: UserCredentialsDataModel,
    ): UserCredentialsDataModel {
        val aead = obtainAead(context)
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
        val aead = obtainAead(context)
        val decryptedUserName = aead.decrypt(Base64.decode(credential.userName), null)
        val decryptedPassword = aead.decrypt(Base64.decode(credential.password), null)
        return UserCredentialsDataModel(
            decryptedUserName.toString(Charsets.UTF_8),
            decryptedPassword.toString(Charsets.UTF_8),
        )
    }

    private fun obtainAead(context: Context): Aead = aead ?: createAead(context).also { aead = it }

    private fun createAead(context: Context): Aead {
        AeadConfig.register()
        return AndroidKeysetManager
            .Builder()
            .withSharedPref(
                context,
                KEYSET_NAME,
                PREF_FILE_NAME,
            ).withKeyTemplate(KeyTemplates.get("AES256_GCM"))
            .withMasterKeyUri(MASTER_KEY_URI)
            .build()
            .keysetHandle
            .getPrimitive(Aead::class.java)
    }
}
