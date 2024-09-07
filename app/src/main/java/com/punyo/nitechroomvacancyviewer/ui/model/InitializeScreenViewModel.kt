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

class InitializeScreenViewModel(application: Application) : AndroidViewModel(application) {
    private val state = MutableStateFlow(InitializeScreenUiState())
    val uiState: StateFlow<InitializeScreenUiState> = state.asStateFlow()

    fun signInWithSavedCredentials() {
        viewModelScope.launch {
            val result = AuthRepository.signInWithSavedCredentials(getApplication())
            if (result == AuthRepository.AuthResultStatus.SUCCESS) {
                state.value = state.value.copy(signedInWithSavedCredentials = true)
            } else {
                state.value = state.value.copy(signedInWithSavedCredentials = false)
            }
        }
    }

    class Factory(private val context: Application) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(InitializeScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return InitializeScreenViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class InitializeScreenUiState(
    val signedInWithSavedCredentials: Boolean? = null,
)