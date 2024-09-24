package com.punyo.nitechvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.data.room.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class HomeComponentViewModel(application: Application, private val roomRepository: RoomRepository) :
    AndroidViewModel(application) {
    private val state = MutableStateFlow(HomeComponentUiState())
    val uiState: StateFlow<HomeComponentUiState> = state.asStateFlow()

    fun onTabSelected(index: Int) {
        state.value = state.value.copy(selectedTabIndex = index)
    }

    fun setRefreshingCurrentVacantRoomsList(isRefreshing: Boolean) {
        state.value = state.value.copy(isRefreshingCurrentVacantRoomsList = isRefreshing)
    }

    fun updateCurrentVacantRoomsArray() {
        val now = LocalDateTime.now()
        viewModelScope.launch {
            val todayRoomsData = roomRepository.getTodayRoomsData(getApplication())
            if (todayRoomsData != null) {
                todayRoomsData.rooms.filter { room ->
                    !room.eventsInfo.any {
                        now.isAfter(it.start) && now.isBefore(it.end)
                    }
                }.let { rooms ->
                    state.value = state.value.copy(currentVacantRooms = rooms.toTypedArray())
                }
            } else {
                state.value = state.value.copy(isTodayRoomsDataNotFoundOnDB = true)
            }
        }
    }

    fun getMinutesUntilNextEvent(room: Room): Int? {
        val now = LocalDateTime.now()
        val nextEvent = room.eventsInfo.filter { event ->
            now.isBefore(event.start)
        }.minByOrNull { it.start }
        return if (nextEvent != null) {
            val diff =
                nextEvent.start.toLocalTime().toSecondOfDay() - now.toLocalTime().toSecondOfDay()
            diff / 60
        } else {
            null
        }
    }


    class Factory(
        private val application: Application,
        private val roomRepository: RoomRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HomeComponentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return HomeComponentViewModel(application, roomRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class HomeComponentUiState(
    val selectedTabIndex: Int = 0,
    val isTodayRoomsDataNotFoundOnDB: Boolean = false,
    val isRefreshingCurrentVacantRoomsList: Boolean = false,
    val currentVacantRooms: Array<Room>? = emptyArray(),
)