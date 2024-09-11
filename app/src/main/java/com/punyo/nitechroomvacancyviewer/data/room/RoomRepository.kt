package com.punyo.nitechroomvacancyviewer.data.room

import android.app.Application
import android.util.Log
import com.punyo.nitechroomvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechroomvacancyviewer.data.room.source.RoomNetworkDataSource

object RoomRepository {
    private val principalNameAndDisplayName = mutableMapOf<String, String>()

    suspend fun getDisplayName(principalName: String): String {
        if (principalNameAndDisplayName.contains(principalName)) {
            return principalNameAndDisplayName[principalName]!!
        } else {

        }
        return ""
    }

    suspend fun login(application: Application) {
        RoomNetworkDataSource.login(application, AuthRepository.currentToken!!)
            ?.let { Log.d("RoomRepository", it) }
    }
}