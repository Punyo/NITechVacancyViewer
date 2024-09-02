package com.punyo.nitechroomvacancyviewer.ui.model

import androidx.compose.runtime.rememberCoroutineScope
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream

class VacancyComponentViewModel(private val buildingRepository: BuildingRepository) : ViewModel() {
    private val state = MutableStateFlow(VacancyComponentUiState())
    val uiState: StateFlow<VacancyComponentUiState> = state.asStateFlow()

    fun getNumberOfVacantRoom(roomPrincipalNames: Array<String>): UInt {
        val numberOfVacantRoom = 0u
        //TODO: Implement this function

        return numberOfVacantRoom
    }

    suspend fun loadBuildings(inputStream: InputStream) {
        val buildings = buildingRepository.getBuildings(inputStream)
        setBuildings(buildings)
    }

    fun setBuildings(buildings: Array<Building>) {
        state.value = VacancyComponentUiState(buildings)
    }

    class Factory(private val buildingRepository: BuildingRepository) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            if (modelClass.isAssignableFrom(VacancyComponentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VacancyComponentViewModel(buildingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class VacancyComponentUiState(
    val buildings: Array<Building>? = null
)