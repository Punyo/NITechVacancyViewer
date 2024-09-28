package com.punyo.nitechvacancyviewer.data.room

import android.app.Application
import com.punyo.nitechvacancyviewer.data.room.model.RoomsDataModel
import com.punyo.nitechvacancyviewer.data.room.source.RoomLocalDatasource
import java.time.LocalDate

class RoomRepository {
    suspend fun isRoomsDataExist(application: Application, date: LocalDate) =
        RoomLocalDatasource.isRoomsDataExist(application, date)

    suspend fun saveToDBFromHTML(application: Application, html: String, date: LocalDate) {
        RoomLocalDatasource.saveOneWeekRoomsDataToDBFromHTML(application, html, date)
    }

    suspend fun getTodayRoomsData(application: Application): RoomsDataModel? {
        val currentDate = LocalDate.now()
        if (currentDate != RoomLocalDatasource.loadedRoomsData?.date) {
            if (isRoomsDataExist(application, currentDate)) {
                RoomLocalDatasource.loadFromDB(application, currentDate)
            } else {
                return null
            }
        }
        return RoomLocalDatasource.loadedRoomsData
    }

    suspend fun clearDB(application: Application) {
        RoomLocalDatasource.clearDB(application)
    }
}