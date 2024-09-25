package com.punyo.nitechvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import com.punyo.nitechvacancyviewer.data.room.model.Room
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.time.LocalDateTime

class HomeComponentViewModel() :
    ViewModel() {
    private val state = MutableStateFlow(HomeComponentUiState())
    val uiState: StateFlow<HomeComponentUiState> = state.asStateFlow()

    fun onTabSelected(index: Int) {
        state.value = state.value.copy(selectedTabIndex = index)
    }

    fun setCurrentVacantRooms(todayRoomsData: Array<Room>) {
        val now = LocalDateTime.now()
        todayRoomsData.filter { room ->
            !room.eventsInfo.any {
                now.isAfter(it.start) && now.isBefore(it.end)
            }
        }.let { rooms ->
            state.value = state.value.copy(currentVacantRooms = rooms.toTypedArray())
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

}

data class HomeComponentUiState(
    val selectedTabIndex: Int = 0,
    val currentVacantRooms: Array<Room> = emptyArray(),
)