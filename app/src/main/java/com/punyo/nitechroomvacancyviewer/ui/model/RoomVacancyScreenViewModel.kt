package com.punyo.nitechroomvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import com.punyo.nitechroomvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechroomvacancyviewer.data.room.model.Room
import java.time.LocalDateTime

class RoomVacancyScreenViewModel : ViewModel() {
    fun getRoomVacancy(room: Room, time: LocalDateTime): RoomVacancyStatus {
        val isTimeWithinEvent = room.eventTimesData.any { eventTime ->
            time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
        }
        return if (isTimeWithinEvent) {
            RoomVacancyStatus.OCCUPY
        } else {
            RoomVacancyStatus.VACANT
        }
    }

    fun getCurrentEvent(room: Room, time: LocalDateTime): EventInfo? {
        return room.eventTimesData.find { eventTime ->
            time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
        }
    }
}