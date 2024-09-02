package com.punyo.nitechroomvacancyviewer.data.building

import com.punyo.nitechroomvacancyviewer.data.building.model.Building
import com.punyo.nitechroomvacancyviewer.data.building.model.BuildingsDataModel
import com.punyo.nitechroomvacancyviewer.data.building.source.BuildingLocalDatasource

class BuildingRepository(private val buildingLocalDatasource: BuildingLocalDatasource) {
    fun getBuildings(jsonString: String): Array<Building> {
        return buildingLocalDatasource.getBuildingsDataFromJson(jsonString).buildingsData
    }
}