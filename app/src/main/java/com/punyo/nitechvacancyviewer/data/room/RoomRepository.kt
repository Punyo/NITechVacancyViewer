package com.punyo.nitechvacancyviewer.data.room

import android.app.Application
import com.punyo.nitechvacancyviewer.data.room.source.RoomLocalDatasource
import java.time.LocalDate

class RoomRepository {
    suspend fun isRoomsDataExist(application: Application, date: LocalDate) =
        RoomLocalDatasource.isRoomsDataExist(application, date)

    suspend fun loadRoomsDataFromDB(application: Application, date: LocalDate) {
        RoomLocalDatasource.loadFromDB(application, date)
    }

    suspend fun saveToDBFromHTML(application: Application, html: String, date: LocalDate) {
        RoomLocalDatasource.saveOneWeekRoomsDataToDBFromHTML(application, html, date)
    }

    fun getLoadedRoomsData() = RoomLocalDatasource.loadedRoomsData?.rooms
}