package com.punyo.nitechroomvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream

class VacancyComponentViewModel(private val buildingRepository: BuildingRepository) : ViewModel() {
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

    fun loadBuildings(inputStream: InputStream) {
        viewModelScope.launch {
            val buildings = buildingRepository.getBuildings(inputStream)
            setBuildings(buildings)
        }
    }

    fun setBuildings(buildings: Array<Building>) {
        state.value = state.value.copy(buildings = buildings)
    }
}

data class VacancyComponentUiState(
    val selectedBuildingIndex: Int? = null,
    val buildings: Array<Building>? = null
)