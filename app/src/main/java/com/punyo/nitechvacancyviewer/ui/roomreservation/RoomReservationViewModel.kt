package com.punyo.nitechvacancyviewer.ui.roomreservation

import androidx.lifecycle.ViewModel
import com.punyo.nitechvacancyviewer.data.room.model.Room
import java.time.LocalDateTime

sealed class NextEventResult {
    data class Known(val minutes: Int) : NextEventResult()
    object Unknown : NextEventResult()
    object None : NextEventResult()
}

class RoomReservationViewModel : ViewModel() {
    fun getMinutesUntilNextEvent(room: Room): NextEventResult {
        val now = LocalDateTime.now()
        val hasUnknownStartEvent = room.eventsInfo.any { it.start == null }
        val nextEvent =
            room.eventsInfo
                .filter { event ->
                    event.start != null && now.isBefore(event.start)
                }.minByOrNull { it.start!! }
        return when {
            nextEvent != null -> {
                val diff =
                    nextEvent.start!!.toLocalTime().toSecondOfDay() - now.toLocalTime().toSecondOfDay()
                NextEventResult.Known(diff / 60)
            }
            hasUnknownStartEvent -> NextEventResult.Unknown
            else -> NextEventResult.None
        }
    }
}
