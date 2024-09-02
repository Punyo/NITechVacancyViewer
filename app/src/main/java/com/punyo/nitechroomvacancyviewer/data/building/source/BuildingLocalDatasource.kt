package com.punyo.nitechroomvacancyviewer.data.building.source

import com.punyo.nitechroomvacancyviewer.data.building.model.BuildingsDataModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class BuildingLocalDatasource {
    fun getBuildingsDataFromJson(jsonString: String): BuildingsDataModel {
        return Json.decodeFromString(jsonString)
    }
}