package com.punyo.nitechroomvacancyviewer.ui.model

import android.app.Activity
import androidx.lifecycle.ViewModel
import com.punyo.nitechroomvacancyviewer.data.MSGraphRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInScreenViewModel(private val msGraphRepository: MSGraphRepository) : ViewModel() {
    private val state= MutableStateFlow(SignInScreenUiState())
    val uiState: StateFlow<SignInScreenUiState> = state.asStateFlow()

    fun onSignInButtonClicked(activity: Activity, onSignInSuccess: () -> Unit) {
        msGraphRepository.signIn(activity = activity) { status ->
            state.value = SignInScreenUiState(signInResultStatus = status)
            if (status == MSGraphRepository.MSALOperationResultStatus.SUCCESS) {
                onSignInSuccess()
            }
        }
    }
}

data class SignInScreenUiState(
    val signInResultStatus: MSGraphRepository.MSALOperationResultStatus? = null,
)