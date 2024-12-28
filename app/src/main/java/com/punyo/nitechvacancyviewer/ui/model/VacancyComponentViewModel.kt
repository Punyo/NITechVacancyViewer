package com.punyo.nitechvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.punyo.nitechvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechvacancyviewer.data.building.model.Building
import com.punyo.nitechvacancyviewer.data.room.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.time.LocalDateTime

class VacancyComponentViewModel(
    private val buildingRepository: BuildingRepository,
) : ViewModel() {
    private val state = MutableStateFlow(VacancyComponentUiState())
    val uiState: StateFlow<VacancyComponentUiState> = state.asStateFlow()

    fun getNumberOfVacantRoom(roomsData: Array<Room>, time: LocalDateTime): UInt {
        return roomsData.filter { room ->
            !room.eventsInfo.any { eventTime ->
                time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
            }
        }.size.toUInt()
    }

    suspend fun loadBuildings(inputStream: InputStream) {
        val buildings = buildingRepository.getBuildings(inputStream)
        state.value = state.value.copy(buildings = buildings)
    }

    class Factory(private val buildingRepository: BuildingRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
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

enum class RoomVacancyStatus {
    VACANT,
    OCCUPY
}