package com.punyo.nitechroomvacancyviewer.data

import java.util.Calendar

data class RoomData(
    val roomPrincipalName: String,
    val roomDisplayName: String,
    val eventTimesData: List<EventTimeData>
)

data class EventTimeData(
    val start: Calendar,
    val end: Calendar
)