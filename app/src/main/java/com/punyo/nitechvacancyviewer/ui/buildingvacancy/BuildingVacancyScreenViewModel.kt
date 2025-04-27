package com.punyo.nitechvacancyviewer.ui.buildingvacancy

import androidx.lifecycle.ViewModel
import com.punyo.nitechvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechvacancyviewer.data.building.model.Building
import com.punyo.nitechvacancyviewer.data.room.model.Room
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.InputStream
import java.time.LocalDateTime
import javax.inject.Inject

@HiltViewModel
class VacancyComponentViewModel
    @Inject
    constructor(
        private val buildingRepository: BuildingRepository,
    ) : ViewModel() {
        private val state = MutableStateFlow(VacancyComponentUiState())
        val uiState: StateFlow<VacancyComponentUiState> = state.asStateFlow()

        fun getNumberOfVacantRoom(
            roomsData: Array<Room>,
            time: LocalDateTime,
        ): UInt =
            roomsData
                .filter { room ->
                    !room.eventsInfo.any { eventTime ->
                        time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
                    }
                }.size
                .toUInt()

        suspend fun loadBuildings(inputStream: InputStream) {
            val buildings = buildingRepository.getBuildings(inputStream)
            state.value = state.value.copy(buildings = buildings)
        }
    }

data class VacancyComponentUiState(
    val buildings: Array<Building>? = null,
)
