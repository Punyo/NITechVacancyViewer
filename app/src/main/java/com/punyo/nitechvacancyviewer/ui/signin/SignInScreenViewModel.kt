package com.punyo.nitechvacancyviewer.ui.signin

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.application.enums.AuthResultStatus
import com.punyo.nitechvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInScreenViewModel
@Inject
constructor(
    private val applicationContext: Context,
    private val roomRepository: RoomRepository,
    private val authRepository: AuthRepository,
) : ViewModel() {
    private val state = MutableStateFlow(SignInScreenUiState())
    val uiState: StateFlow<SignInScreenUiState> = state.asStateFlow()

    fun onSignInButtonClicked(
        onSignInSuccess: () -> Unit,
        demoModeUserNameAndPassword: String,
    ) {
        if (state.value.userName.isNotEmpty() && state.value.password.isNotEmpty()) {
            if (state.value.userName == demoModeUserNameAndPassword && state.value.password == demoModeUserNameAndPassword) {
                roomRepository.setDemoMode(true)
                onSignInSuccess()
            } else {
                state.value = state.value.copy(isSignInButtonEnabled = false, signInResult = null)
                viewModelScope.launch {
                    val result =
                        authRepository.signIn(
                            applicationContext,
                            state.value.userName,
                            state.value.password,
                        )
                    if (result == AuthResultStatus.SUCCESS) {
                        onSignInSuccess()
                    }
                    state.value = state.value.copy(signInResult = result)
                }
            }
        } else {
            checkTextFieldsEmptiness()
        }
    }

    private fun checkTextFieldsEmptiness() {
        state.value =
            state.value.copy(
                isErrorUserName = state.value.userName.isEmpty(),
                isErrorPassword = state.value.password.isEmpty(),
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
}

data class SignInScreenUiState(
    val userName: String = "",
    val password: String = "",
    val isErrorUserName: Boolean = false,
    val isErrorPassword: Boolean = false,
    val isSignInButtonEnabled: Boolean = true,
    val signInResult: AuthResultStatus? = null,
)
