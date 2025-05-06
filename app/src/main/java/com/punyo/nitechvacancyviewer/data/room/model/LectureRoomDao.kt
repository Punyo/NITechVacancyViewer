package com.punyo.nitechvacancyviewer.data.room.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LectureRoomDao {
    @Insert
    suspend fun insert(lectureRoomEntity: LectureRoomEntity)

    @Query("DELETE FROM lecture_room")
    suspend fun deleteAll()

    @Query("SELECT * FROM lecture_room WHERE date = :date")
    suspend fun getByDate(date: String): Array<LectureRoomEntity>

    @Query("SELECT * FROM lecture_room")
    suspend fun getAll(): Array<LectureRoomEntity>

    @Query("SELECT EXISTS(SELECT * FROM lecture_room WHERE date = :date)")
    suspend fun isDataExistByDate(date: String): Int
}
