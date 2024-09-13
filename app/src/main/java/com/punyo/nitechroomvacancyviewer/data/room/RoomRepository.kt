package com.punyo.nitechroomvacancyviewer.data.room

import com.punyo.nitechroomvacancyviewer.data.room.source.RoomLocalDatasource

class RoomRepository {
    fun loadRoomsDataFromHTML(html: String) {
        RoomLocalDatasource.loadFromHTML(html)
    }

    fun getRoomsData() = RoomLocalDatasource.loadedRoomsData?.rooms
}