package com.punyo.nitechroomvacancyviewer.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.microsoft.identity.client.exception.MsalException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class InitializeScreenViewModel(private val msGraphRepository: MSGraphRepository) : ViewModel() {
    private val state = MutableStateFlow(InitializeScreenUiState())
    val uiState: StateFlow<InitializeScreenUiState> = state.asStateFlow()

    fun initMSAL(context: Context) {
        viewModelScope.launch {
            val result = msGraphRepository.initMSAL(context)
            when (result.status) {
                MSGraphRepository.MSALInitResultStatus.INIT_SUCCESS_AND_SIGNED_IN -> {
                    state.value = InitializeScreenUiState(alreadySignedIn = true)
                }

                MSGraphRepository.MSALInitResultStatus.INIT_SUCCESS_AND_NOT_SIGNED_IN -> {
                    state.value = InitializeScreenUiState(alreadySignedIn = false)
                }

                MSGraphRepository.MSALInitResultStatus.INIT_FAILED -> {
                    state.value =
                        InitializeScreenUiState(
                            thrownException = result.exception,
                            alreadySignedIn = null
                        )
                }
            }
        }
    }
}

data class InitializeScreenUiState(
    val alreadySignedIn: Boolean? = null,
    val thrownException: MsalException? = null
)