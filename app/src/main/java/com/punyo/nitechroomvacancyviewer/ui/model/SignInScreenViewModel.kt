package com.punyo.nitechroomvacancyviewer.ui.model

import android.app.Activity
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SignInScreenViewModel() : ViewModel() {
    private val state= MutableStateFlow(SignInScreenUiState())
    val uiState: StateFlow<SignInScreenUiState> = state.asStateFlow()

    fun onSignInButtonClicked(activity: Activity, onSignInSuccess: () -> Unit) {

    }

    fun setInitSuccess(isInitSuccess: Boolean) {
        state.value = state.value.copy(isInitSuccess = isInitSuccess)
    }
}

data class SignInScreenUiState(
    val isInitSuccess: Boolean? = null
)