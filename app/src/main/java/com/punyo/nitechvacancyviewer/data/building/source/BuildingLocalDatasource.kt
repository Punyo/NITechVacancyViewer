package com.punyo.nitechvacancyviewer.data.building.source

import com.punyo.nitechvacancyviewer.application.GsonInstance
import com.punyo.nitechvacancyviewer.data.building.model.BuildingsDataModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStream

class BuildingLocalDatasource {
    suspend fun getBuildingsDataFromJson(inputStream: InputStream): BuildingsDataModel =
        withContext(Dispatchers.IO) {
            val bufferedReader = BufferedReader(inputStream.reader())
            val jsonString = bufferedReader.use { it.readText() }
            GsonInstance.gson.fromJson(jsonString, BuildingsDataModel::class.java)
        }
}
