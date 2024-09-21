package com.punyo.nitechvacancyviewer.data.room

import android.app.Application
import com.punyo.nitechvacancyviewer.data.room.source.RoomLocalDatasource
import java.time.MonthDay

class RoomRepository {
    fun loadRoomsDataFromHTML(html: String) {
        RoomLocalDatasource.loadFromHTML(html)
    }

    suspend fun isRoomsDataExist(application: Application, monthDay: MonthDay) =
        RoomLocalDatasource.isRoomsDataExist(application, monthDay)

    suspend fun loadRoomsDataFromDB(application: Application, monthDay: MonthDay) {
        RoomLocalDatasource.loadFromDB(application, monthDay)
    }

    suspend fun saveToDBFromHTML(application: Application, html: String, monthDay: MonthDay) {
        RoomLocalDatasource.saveToDBFromHTML(application, html, monthDay)
    }

    fun getLoadedRoomsData() = RoomLocalDatasource.loadedRoomsData?.rooms
}