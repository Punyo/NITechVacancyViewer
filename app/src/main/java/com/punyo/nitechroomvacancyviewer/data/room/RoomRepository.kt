package com.punyo.nitechroomvacancyviewer.data.room

import com.punyo.nitechroomvacancyviewer.data.building.model.Building

class RoomRepository {
    private val principalNameAndDisplayName = mutableMapOf<String, String>()

    suspend fun getDisplayName(principalName: String): String {
        if (principalNameAndDisplayName.contains(principalName)) {
            return principalNameAndDisplayName[principalName]!!
        }else{

        }
        return ""
    }
}