package com.punyo.nitechroomvacancyviewer.data.building.source

import com.punyo.nitechroomvacancyviewer.GsonInstance
import com.punyo.nitechroomvacancyviewer.data.building.model.BuildingsDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream

class BuildingLocalDatasource {
    suspend fun getBuildingsDataFromJson(inputStream: InputStream): BuildingsDataModel {
        return withContext(Dispatchers.IO) {
            val bufferedReader = BufferedReader(inputStream.reader())
            val jsonString = bufferedReader.use { it.readText() }
            GsonInstance.gson.fromJson(jsonString, BuildingsDataModel::class.java)
        }
    }
}