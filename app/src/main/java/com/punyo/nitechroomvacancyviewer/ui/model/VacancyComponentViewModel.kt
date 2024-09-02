package com.punyo.nitechroomvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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

    fun getBuildings(inputStream: InputStream): Array<Building> {
        return BufferedReader(inputStream.reader()).use { reader ->
            buildingRepository.getBuildings(reader.readText())
        }
    }
}

data class VacancyComponentUiState(
    val selectedBuildingIndex: Int = 0
)