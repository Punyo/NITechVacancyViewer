package com.punyo.nitechvacancyviewer.data.building

import com.punyo.nitechvacancyviewer.data.building.model.Building
import com.punyo.nitechvacancyviewer.data.building.source.BuildingLocalDatasource
import java.io.InputStream

class BuildingRepository(private val buildingLocalDatasource: BuildingLocalDatasource) {
    suspend fun getBuildings(inputStream: InputStream): Array<Building> {
        val buildingsDataModel = buildingLocalDatasource.getBuildingsDataFromJson(inputStream)
        return buildingsDataModel.buildingsData
    }
}