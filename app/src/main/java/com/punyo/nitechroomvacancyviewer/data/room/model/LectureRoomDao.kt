package com.punyo.nitechroomvacancyviewer.data.room.model

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LectureRoomDao {
    @Insert
    fun insert(lectureRoomEntity: LectureRoomEntity)

    @Query("DELETE FROM lecture_room")
    fun deleteAll()

    @Query("SELECT * FROM lecture_room WHERE date = :date")
    fun getByDate(date: String): Array<LectureRoomEntity>

    @Query("SELECT EXISTS(SELECT * FROM lecture_room WHERE date = :date)")
    fun isDataExistByDate(date: String): Int
}