package com.punyo.nitechvacancyviewer.data.room

import android.content.Context
import com.punyo.nitechvacancyviewer.data.room.model.RoomsDataModel
import com.punyo.nitechvacancyviewer.data.room.source.RoomLocalDataSource
import java.time.LocalDate
import javax.inject.Inject

class RoomRepositoryImpl
    @Inject
    constructor(
        private val roomLocalDatasource: RoomLocalDataSource,
    ) : RoomRepository {
        private var isDemoMode: Boolean = false

        override fun setDemoMode(isDemoMode: Boolean) {
            this.isDemoMode = isDemoMode
        }

        override suspend fun isRoomsDataExist(
            applicationContext: Context,
            date: LocalDate,
        ): Boolean =
            if (!isDemoMode) {
                roomLocalDatasource.isRoomsDataExist(applicationContext, date)
            } else {
                true
            }

        override suspend fun saveToDBFromHTML(
            applicationContext: Context,
            html: String,
            date: LocalDate,
        ) {
            if (!isDemoMode) {
                roomLocalDatasource.saveOneWeekRoomsDataToDBFromHTML(applicationContext, html, date)
            }
        }

        override suspend fun getTodayRoomsData(applicationContext: Context): RoomsDataModel? {
            if (!isDemoMode) {
                val currentDate = LocalDate.now()
                if (currentDate != roomLocalDatasource.loadedRoomsData?.date) {
                    if (isRoomsDataExist(applicationContext, currentDate)) {
                        roomLocalDatasource.loadFromDB(applicationContext, currentDate)
                    } else {
                        return null
                    }
                }
                return roomLocalDatasource.loadedRoomsData
            } else {
                return roomLocalDatasource.getDemoRoomsData()
            }
        }

        override suspend fun clearDB(applicationContext: Context) {
            roomLocalDatasource.clearDB(applicationContext)
        }
    }
