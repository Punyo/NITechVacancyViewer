package com.punyo.nitechroomvacancyviewer.data.room

import android.app.Application
import com.punyo.nitechroomvacancyviewer.data.room.source.RoomLocalDatasource
import java.time.MonthDay

class RoomRepository {
    fun loadRoomsDataFromHTML(html: String) {
        RoomLocalDatasource.loadFromHTML(html)
    }

    fun loadRoomsDataFromDB(application: Application, monthDay: MonthDay) {
        RoomLocalDatasource.loadFromDB(application, monthDay)
    }

    fun saveToDBFromHTML(application: Application, html: String, monthDay: MonthDay) {
        RoomLocalDatasource.saveToDBFromHTML(application, html, monthDay)
    }

    fun getRoomsData() = RoomLocalDatasource.loadedRoomsData?.rooms
}