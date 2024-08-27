package com.punyo.nitechroomvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class VacancyComponentViewModel : ViewModel() {
    private val state = MutableStateFlow(VacancyComponentUiState())
    val uiState: StateFlow<VacancyComponentUiState> = state.asStateFlow()

    fun onBuildingSelected(index: Int) {
        state.value = state.value.copy(selectedBuildingIndex = index)
    }

    fun getNumberOfVacantRoom(roomPrincipalNames: Array<String>): UInt {
        val numberOfVacantRoom = 0u
        //TODO: Implement this function

        return numberOfVacantRoom
    }
}

data class VacancyComponentUiState(
    val selectedBuildingIndex: Int = 0
)