package com.punyo.nitechvacancyviewer.ui.accessibility

import android.content.Context
import androidx.lifecycle.ViewModel
import com.punyo.nitechvacancyviewer.data.widget.WidgetRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class AccessibilityConsentScreenViewModel
@Inject
constructor(
    private val applicationContext: Context,
    private val widgetRepository: WidgetRepository,
) : ViewModel() {
    private val state = MutableStateFlow(AccessibilityConsentScreenUiState())
    val uiState: StateFlow<AccessibilityConsentScreenUiState> = state.asStateFlow()

    fun setUserConsentState(userConsentState: Boolean) {
        state.value = state.value.copy(userConsentState = userConsentState)
    }

    fun resetUserConsentState() {
        state.value = state.value.copy(userConsentState = null)
    }

    suspend fun isAccessibilityServiceEnabled() =
        widgetRepository.isAccessibilityServiceEnabled(applicationContext)
}

data class AccessibilityConsentScreenUiState(
    val userConsentState: Boolean? = null,
)
