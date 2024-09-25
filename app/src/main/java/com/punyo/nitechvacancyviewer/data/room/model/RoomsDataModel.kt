package com.punyo.nitechvacancyviewer.data.room.model

import com.punyo.nitechvacancyviewer.ui.CommonDateTimeFormater
import java.time.LocalDate
import java.time.LocalDateTime

data class RoomsDataModel(
    val rooms: Array<Room>,
    val date: LocalDate
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as RoomsDataModel

        return rooms.contentEquals(other.rooms)
    }

    override fun hashCode(): Int {
        return rooms.contentHashCode()
    }
}

data class Room(
    val roomDisplayName: String,
    val eventsInfo: Array<EventInfo>
)

data class EventInfo(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val eventDescription: String = ""
) {
    fun getStartAndEndString(): String {
        val formatter = CommonDateTimeFormater.formatter
        return "${start.format(formatter)} ～ ${end.format(formatter)}"
    }
}