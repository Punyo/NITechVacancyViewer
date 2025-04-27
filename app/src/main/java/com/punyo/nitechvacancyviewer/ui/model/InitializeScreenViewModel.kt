package com.punyo.nitechvacancyviewer.ui.model

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.application.enums.AuthResultStatus
import com.punyo.nitechvacancyviewer.data.auth.AuthRepositoryImpl
import com.punyo.nitechvacancyviewer.data.room.RoomRepositoryImpl
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class InitializeScreenViewModel
    @Inject
    constructor(
        private val applicationContext: Context,
        private val roomRepository: RoomRepositoryImpl,
        private val authRepository: AuthRepositoryImpl,
    ) : ViewModel() {
        private val state = MutableStateFlow(InitializeScreenUiState())
        val uiState: StateFlow<InitializeScreenUiState> = state.asStateFlow()

        fun initialize() {
            checkAlreadySignedIn()
            tryToLoadSavedRoomsData()
        }

        private fun checkAlreadySignedIn() {
            if (authRepository.currentToken != null) {
                state.value = state.value.copy(signedInWithSavedCredentialsOrAlreadySignedIn = true)
            }
        }

        private fun tryToLoadSavedRoomsData() {
            viewModelScope.launch(Dispatchers.IO) {
                val date = LocalDate.now()
                if (roomRepository.isRoomsDataExist(applicationContext, date)) {
                    state.value = state.value.copy(loadedRoomsDataFromDB = true)
                } else {
                    state.value = state.value.copy(loadedRoomsDataFromDB = false)
                }
            }
        }

        fun tryToSignInWithSavedCredentials() {
            if (authRepository.currentToken == null) {
                viewModelScope.launch {
                    val result = authRepository.signInWithSavedCredentials(applicationContext)
                    if (result == AuthResultStatus.SUCCESS) {
                        state.value =
                            state.value.copy(signedInWithSavedCredentialsOrAlreadySignedIn = true)
                    } else {
                        state.value =
                            state.value.copy(signedInWithSavedCredentialsOrAlreadySignedIn = false)
                    }
                }
            } else {
                state.value = state.value.copy(signedInWithSavedCredentialsOrAlreadySignedIn = true)
            }
        }

        fun activateCampusSquareWebView() {
            state.value = state.value.copy(isActivatedCampusSquareWebView = true)
        }

        fun tryToLoadRoomsDataFromHTML(html: String) {
            viewModelScope.launch {
                val date = LocalDate.now()
                runCatching {
                    roomRepository.saveToDBFromHTML(applicationContext, html, date)
                }.onSuccess {
                    state.value = state.value.copy(loadedRoomsDataFromCampusSquare = true)
                }.onFailure {
                    Log.e("RoomLocalDatasource", it.stackTraceToString())
                    state.value = state.value.copy(loadedRoomsDataFromCampusSquare = false)
                }
            }
        }

        fun changeAskForSignInDialogVisibility(visible: Boolean) {
            state.value = state.value.copy(showAskForSignInDialog = visible)
        }

        fun getCurrentToken(): String? = authRepository.currentToken

        fun currentErrorShowed() {
            state.value = state.value.copy(errorMessage = null)
        }

        fun setErrorMessage(
            message: String,
            actionLabel: String,
            onClickAction: () -> Unit,
        ) {
            state.value =
                state.value.copy(errorMessage = ErrorMessage(message, actionLabel, onClickAction))
        }
    }

data class ErrorMessage(
    val message: String,
    val actionLabel: String,
    val onClickAction: () -> Unit,
)

data class InitializeScreenUiState(
    val signedInWithSavedCredentialsOrAlreadySignedIn: Boolean? = null,
    val loadedRoomsDataFromDB: Boolean? = null,
    val loadedRoomsDataFromCampusSquare: Boolean? = null,
    val isActivatedCampusSquareWebView: Boolean = false,
    val showAskForSignInDialog: Boolean = false,
    val errorMessage: ErrorMessage? = null,
)
