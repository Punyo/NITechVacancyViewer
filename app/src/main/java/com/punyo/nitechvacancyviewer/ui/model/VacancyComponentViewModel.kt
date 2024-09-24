package com.punyo.nitechvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.punyo.nitechvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechvacancyviewer.data.building.model.Building
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.data.room.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class VacancyComponentViewModel(
    application: Application,
    private val buildingRepository: BuildingRepository,
    private val roomRepository: RoomRepository
) : AndroidViewModel(application) {
    private val dateTimeFormatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("HH:mm")
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

    suspend fun updateRoomVacancy() {
        val roomsData = roomRepository.getTodayRoomsData(getApplication())
        if (roomsData != null) {
            state.value =
                state.value.copy(roomsData = roomsData.rooms, lastUpdateTime = LocalDateTime.now())
        } else {
            state.value = state.value.copy(isTodayRoomsDataNotFoundOnDB = true)
        }
    }

    fun setRefreshing(isRefreshing: Boolean) {
        state.value = state.value.copy(isRefreshing = isRefreshing)
    }

    fun getLastUpdateTimeString(time: LocalDateTime): String {
        return time.format(dateTimeFormatter)
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
    val lastUpdateTime: LocalDateTime? = null,
    val isTodayRoomsDataNotFoundOnDB: Boolean = false,
    val isRefreshing: Boolean = false
)

enum class RoomVacancyStatus {
    VACANT,
    OCCUPY
}