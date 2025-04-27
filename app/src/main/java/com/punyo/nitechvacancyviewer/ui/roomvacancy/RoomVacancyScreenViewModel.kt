package com.punyo.nitechvacancyviewer.ui.roomvacancy

import androidx.lifecycle.ViewModel
import com.punyo.nitechvacancyviewer.application.enums.RoomVacancyStatus
import com.punyo.nitechvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechvacancyviewer.data.room.model.Room
import java.time.LocalDateTime

class RoomVacancyScreenViewModel : ViewModel() {
    fun getRoomVacancy(
        room: Room,
        time: LocalDateTime,
    ): RoomVacancyStatus {
        val isTimeWithinEvent =
            room.eventsInfo.any { eventTime ->
                time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
            }
        return if (isTimeWithinEvent) {
            RoomVacancyStatus.OCCUPY
        } else {
            RoomVacancyStatus.VACANT
        }
    }

    fun getCurrentEvent(
        room: Room,
        time: LocalDateTime,
    ): EventInfo? =
        room.eventsInfo.find { eventTime ->
            time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
        }
}
