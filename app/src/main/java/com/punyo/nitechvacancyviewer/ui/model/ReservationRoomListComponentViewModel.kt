package com.punyo.nitechvacancyviewer.ui.model

import androidx.lifecycle.ViewModel
import com.punyo.nitechvacancyviewer.data.room.model.Room
import java.time.LocalDateTime

class ReservationRoomListComponentViewModel : ViewModel() {
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