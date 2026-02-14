package com.punyo.nitechvacancyviewer.data.room.model

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [LectureRoomEntity::class], version = 1)
abstract class LectureRoomDatabase : RoomDatabase() {
    abstract fun lectureRoomDao(): LectureRoomDao
}
