package com.punyo.nitechroomvacancyviewer.data.building

import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import com.punyo.nitechroomvacancyviewer.data.building.model.BuildingsDataModel
import com.punyo.nitechroomvacancyviewer.data.building.source.BuildingLocalDatasource
import java.io.InputStream

class BuildingRepository(private val buildingLocalDatasource: BuildingLocalDatasource) {
    suspend fun getBuildings(inputStream: InputStream): Array<Building> {
        val buildingsDataModel = buildingLocalDatasource.getBuildingsDataFromJson(inputStream)
        return buildingsDataModel.buildingsData
    }
}