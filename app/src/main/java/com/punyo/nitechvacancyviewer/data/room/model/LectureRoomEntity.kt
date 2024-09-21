package com.punyo.nitechvacancyviewer.data.room.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "lecture_room")
data class LectureRoomEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "unique_id")
    val uniqueId: Int = 0,
    @ColumnInfo(name = "date")
    val monthDay: String,
    @ColumnInfo(name = "display_name")
    val roomDisplayName: String,
    @ColumnInfo(name = "events_info_json")
    val eventsInfoJSON: String
)