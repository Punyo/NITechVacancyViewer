package com.punyo.nitechvacancyviewer.ui.model

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.application.CommonDateTimeFormater
import com.punyo.nitechvacancyviewer.data.room.RoomRepositoryImpl
import com.punyo.nitechvacancyviewer.data.room.model.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class MainScreenViewModel
    @Inject
    constructor(
        private val applicationContext: Context,
        private val roomRepository: RoomRepositoryImpl,
    ) : ViewModel() {
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
                val roomsData = roomRepository.getTodayRoomsData(applicationContext)
                if (roomsData != null) {
                    state.value =
                        state.value.copy(
                            roomsData = roomsData.rooms,
                            lastVacancyUpdateTime = LocalDateTime.now(),
                        )
                } else {
                    state.value = state.value.copy(isTodayRoomsDataNotFoundOnDB = true)
                }
            }
        }

        fun getLastUpdateTimeString(): String = state.value.lastVacancyUpdateTime!!.format(CommonDateTimeFormater.formatter)
    }

data class MainScreenUiState(
    val currentNavIndex: Int = 0,
    val isRefreshVacancy: Boolean = false,
    val isTodayRoomsDataNotFoundOnDB: Boolean = false,
    val lastVacancyUpdateTime: LocalDateTime? = null,
    val roomsData: Array<Room>? = null,
)
