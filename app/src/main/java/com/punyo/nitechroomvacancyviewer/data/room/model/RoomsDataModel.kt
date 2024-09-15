package com.punyo.nitechroomvacancyviewer.data.room.model

import java.time.LocalDateTime

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
    val roomDisplayName: String,
    val eventsInfo: Array<EventInfo>
)

data class EventInfo(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val eventDescription: String = ""
)