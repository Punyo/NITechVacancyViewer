package com.punyo.nitechroomvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechroomvacancyviewer.data.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SignInScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val state = MutableStateFlow(SignInScreenUiState())
    val uiState: StateFlow<SignInScreenUiState> = state.asStateFlow()

    fun onSignInButtonClicked(onSignInSuccess: () -> Unit) {
        if (state.value.userName.isNotEmpty() && state.value.password.isNotEmpty()) {
            state.value = state.value.copy(isSignInButtonEnabled = false, signInResult = null)
            viewModelScope.launch {
                val result =
                    AuthRepository.signIn(
                        getApplication(),
                        state.value.userName,
                        state.value.password
                    )
                if (result == AuthRepository.AuthResultStatus.SUCCESS) {
                    onSignInSuccess()
                }
                state.value = state.value.copy(signInResult = result)
            }
        } else {
            checkTextFieldsEmptiness()
        }
    }

    private fun checkTextFieldsEmptiness() {
        state.value = state.value.copy(
            isErrorUserName = state.value.userName.isEmpty(),
            isErrorPassword = state.value.password.isEmpty()
        )
    }

    fun setUserName(userName: String) {
        state.value = state.value.copy(userName = userName)
        checkTextFieldsEmptiness()
    }

    fun setPassword(password: String) {
        state.value = state.value.copy(password = password)
        checkTextFieldsEmptiness()
    }

    fun setSignInButtonEnabled(isEnabled: Boolean) {
        state.value = state.value.copy(isSignInButtonEnabled = isEnabled)
    }

    class Factory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SignInScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SignInScreenViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class SignInScreenUiState(
    val userName: String = "",
    val password: String = "",
    val isErrorUserName: Boolean = false,
    val isErrorPassword: Boolean = false,
    val isSignInButtonEnabled: Boolean = true,
    val signInResult: AuthRepository.AuthResultStatus? = null
)