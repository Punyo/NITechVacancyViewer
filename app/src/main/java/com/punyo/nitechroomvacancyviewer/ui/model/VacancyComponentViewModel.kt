package com.punyo.nitechroomvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import com.punyo.nitechroomvacancyviewer.data.room.RoomRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream

class VacancyComponentViewModel(
    application: Application,
    private val buildingRepository: BuildingRepository
) : AndroidViewModel(application) {
    private val state = MutableStateFlow(VacancyComponentUiState())
    val uiState: StateFlow<VacancyComponentUiState> = state.asStateFlow()

    fun getNumberOfVacantRoom(roomPrincipalNames: Array<String>): UInt {
        val numberOfVacantRoom = 0u
        //TODO: Implement this function

        return numberOfVacantRoom
    }

    fun onGetReservationTableHTML(html: String) {

    }

    suspend fun loadBuildings(inputStream: InputStream) {
        val buildings = buildingRepository.getBuildings(inputStream)
        setBuildings(buildings)
    }

    fun setBuildings(buildings: Array<Building>) {
        state.value = VacancyComponentUiState(buildings)
    }

    fun login() {
        viewModelScope.launch {
            withContext(Dispatchers.Main) {
//                RoomRepository.login(getApplication())
            }
        }
    }

    class Factory(
        private val application: Application,
        private val buildingRepository: BuildingRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VacancyComponentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VacancyComponentViewModel(application, buildingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class VacancyComponentUiState(
    val buildings: Array<Building>? = null
)