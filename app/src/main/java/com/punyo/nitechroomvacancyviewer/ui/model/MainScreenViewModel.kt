package com.punyo.nitechroomvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MainScreenViewModel : ViewModel() {
    private val state= MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = state.asStateFlow()

    fun onNavItemClick(index: Int) {
        state.value = state.value.copy(currentNavIndex = index)
    }
}

data class MainScreenUiState(
    val currentNavIndex: Int = 0
)