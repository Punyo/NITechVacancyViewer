package com.punyo.nitechvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.ui.CommonDateTimeFormater
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class MainScreenViewModel(application: Application, private val roomRepository: RoomRepository) :
    AndroidViewModel(application) {
    private val state = MutableStateFlow(MainScreenUiState())
    val uiState: StateFlow<MainScreenUiState> = state.asStateFlow()

    fun onNavItemClick(index: Int) {
        state.value = state.value.copy(currentNavIndex = index)
    }

    fun onRefreshVacancy(refreshDelayMillisecond: Long) {
        state.value = state.value.copy(isRefreshVacancy = true)
        viewModelScope.launch {
            updateVacancy()
            delay(refreshDelayMillisecond)
            state.value = state.value.copy(isRefreshVacancy = false)
        }
    }

    fun updateVacancy() {
        viewModelScope.launch(Dispatchers.IO) {
            val roomsData = roomRepository.getTodayRoomsData(getApplication())
            if (roomsData != null) {
                state.value =
                    state.value.copy(
                        roomsData = roomsData.rooms,
                        lastVacancyUpdateTime = LocalDateTime.now()
                    )
            } else {
                state.value = state.value.copy(isTodayRoomsDataNotFoundOnDB = true)
            }
        }
    }

    fun getLastUpdateTimeString(): String {
        return state.value.lastVacancyUpdateTime!!.format(CommonDateTimeFormater.formatter)
    }

    class Factory(private val context: Application, private val roomRepository: RoomRepository) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainScreenViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainScreenViewModel(context, roomRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class MainScreenUiState(
    val currentNavIndex: Int = 0,
    val isRefreshVacancy: Boolean = false,
    val isTodayRoomsDataNotFoundOnDB: Boolean = false,
    val lastVacancyUpdateTime: LocalDateTime? = null,
    val roomsData: Array<Room>? = null
)