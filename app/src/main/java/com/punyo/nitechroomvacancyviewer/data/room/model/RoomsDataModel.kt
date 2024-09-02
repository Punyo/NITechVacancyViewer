package com.punyo.nitechroomvacancyviewer.data.room.model

import java.util.Calendar

data class RoomsDataModel(
    val rooms: Array<Room>
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
    val roomPrincipalName: String,
    val roomDisplayName: String,
    val eventTimesData: List<EventTime>
)

data class EventTime(
    val start: Calendar,
    val end: Calendar
)