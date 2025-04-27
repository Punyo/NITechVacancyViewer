package com.punyo.nitechvacancyviewer.data.room

import android.content.Context
import com.punyo.nitechvacancyviewer.data.room.model.RoomsDataModel
import java.time.LocalDate

interface RoomRepository {
    fun setDemoMode(isDemoMode: Boolean)

    suspend fun isRoomsDataExist(
        applicationContext: Context,
        date: LocalDate,
    ): Boolean

    suspend fun saveToDBFromHTML(
        applicationContext: Context,
        html: String,
        date: LocalDate,
    )

    suspend fun getTodayRoomsData(applicationContext: Context): RoomsDataModel?

    suspend fun clearDB(applicationContext: Context)
}
