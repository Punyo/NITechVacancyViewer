package com.punyo.nitechvacancyviewer.ui.model

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate

class InitializeScreenViewModel(
    application: Application,
    private val roomRepository: RoomRepository
) : AndroidViewModel(application) {
    private val state = MutableStateFlow(InitializeScreenUiState())
    val uiState: StateFlow<InitializeScreenUiState> = state.asStateFlow()

    fun initialize() {
        checkAlreadySignedIn()
        tryToLoadSavedRoomsData()
    }

    private fun checkAlreadySignedIn() {
        if (AuthRepository.currentToken != null) {
            state.value = state.value.copy(signedInWithSavedCredentialsOrAlreadySignedIn = true)
        }
    }

    private fun tryToLoadSavedRoomsData() {
        viewModelScope.launch {
            val date = LocalDate.now()
            if (roomRepository.isRoomsDataExist(getApplication(), date)) {
                state.value = state.value.copy(loadedRoomsDataFromDB = true)
            } else {
                state.value = state.value.copy(loadedRoomsDataFromDB = false)
            }
        }
    }

    fun tryToSignInWithSavedCredentials() {
        if (AuthRepository.currentToken == null) {
            viewModelScope.launch {
                val result = AuthRepository.signInWithSavedCredentials(getApplication())
                if (result == AuthRepository.AuthResultStatus.SUCCESS) {
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
                roomRepository.saveToDBFromHTML(getApplication(), html, date)
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

    fun getCurrentToken(): String? {
        return AuthRepository.currentToken
    }

    fun currentErrorShowed() {
        state.value = state.value.copy(errorMessage = null)
    }

    fun setErrorMessage(message: String, actionLabel: String, onClickAction: () -> Unit) {
        state.value =
            state.value.copy(errorMessage = ErrorMessage(message, actionLabel, onClickAction))
    }

    class Factory(private val context: Application, private val roomRepository: RoomRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InitializeScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InitializeScreenViewModel(context, roomRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class ErrorMessage(
    val message: String,
    val actionLabel: String,
    val onClickAction: () -> Unit
)

data class InitializeScreenUiState(
    val signedInWithSavedCredentialsOrAlreadySignedIn: Boolean? = null,
    val loadedRoomsDataFromDB: Boolean? = null,
    val loadedRoomsDataFromCampusSquare: Boolean? = null,
    val isActivatedCampusSquareWebView: Boolean = false,
    val showAskForSignInDialog: Boolean = false,
    val errorMessage: ErrorMessage? = null
)