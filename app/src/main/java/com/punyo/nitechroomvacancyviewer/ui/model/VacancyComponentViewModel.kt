package com.punyo.nitechroomvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.punyo.nitechroomvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import com.punyo.nitechroomvacancyviewer.data.room.RoomRepository
import com.punyo.nitechroomvacancyviewer.data.room.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.time.LocalDateTime

class VacancyComponentViewModel(
    application: Application,
    private val buildingRepository: BuildingRepository,
    private val roomRepository: RoomRepository
) : AndroidViewModel(application) {
    private val state = MutableStateFlow(VacancyComponentUiState())
    val uiState: StateFlow<VacancyComponentUiState> = state.asStateFlow()
    val sso4cookie = AuthRepository.currentToken

    fun getNumberOfVacantRoom(roomsData: Array<Room>, time: LocalDateTime): UInt {
        return roomsData.filter { room ->
            !room.eventTimesData.any { eventTime ->
                time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
            }
        }.size.toUInt()
    }

    fun loadRoomsDataFromHTML(html: String) {
        roomRepository.loadRoomsDataFromHTML(html)
        state.value = state.value.copy(roomsData = roomRepository.getRoomsData())
    }

    suspend fun loadBuildings(inputStream: InputStream) {
        val buildings = buildingRepository.getBuildings(inputStream)
        setBuildings(buildings)
    }

    fun setBuildings(buildings: Array<Building>) {
        state.value = state.value.copy(buildings = buildings)
    }

    class Factory(
        private val application: Application,
        private val buildingRepository: BuildingRepository,
        private val roomRepository: RoomRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(VacancyComponentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return VacancyComponentViewModel(
                    application,
                    buildingRepository,
                    roomRepository
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class VacancyComponentUiState(
    val buildings: Array<Building>? = null,
    val roomsData: Array<Room>? = null,
    val updateMinute: Int = 1
)

enum class RoomVacancyStatus {
    VACANT,
    OCCUPY
}