package com.punyo.nitechvacancyviewer.data.room

import android.app.Application
import com.punyo.nitechvacancyviewer.data.room.model.RoomsDataModel
import com.punyo.nitechvacancyviewer.data.room.source.RoomLocalDatasource
import java.time.LocalDate

class RoomRepository {
    companion object {
        private var isDemoMode: Boolean = false

        fun setDemoMode(isDemoMode: Boolean) {
            RoomRepository.isDemoMode = isDemoMode
        }
    }


    suspend fun isRoomsDataExist(application: Application, date: LocalDate): Boolean {
        return if (!isDemoMode) {
            RoomLocalDatasource.isRoomsDataExist(application, date)
        } else {
            true
        }
    }

    suspend fun saveToDBFromHTML(application: Application, html: String, date: LocalDate) {
        if (!isDemoMode) {
            RoomLocalDatasource.saveOneWeekRoomsDataToDBFromHTML(application, html, date)
        }
    }

    suspend fun getTodayRoomsData(application: Application): RoomsDataModel? {
        if (!isDemoMode) {
            val currentDate = LocalDate.now()
            if (currentDate != RoomLocalDatasource.loadedRoomsData?.date) {
                if (isRoomsDataExist(application, currentDate)) {
                    RoomLocalDatasource.loadFromDB(application, currentDate)
                } else {
                    return null
                }
            }
            return RoomLocalDatasource.loadedRoomsData
        } else {
            return RoomLocalDatasource.getDemoRoomsData()
        }
    }

    suspend fun clearDB(application: Application) {
        RoomLocalDatasource.clearDB(application)
    }
}