package com.punyo.nitechvacancyviewer.data.building

import com.punyo.nitechvacancyviewer.data.building.model.Building
import com.punyo.nitechvacancyviewer.data.building.source.BuildingLocalDataSource
import java.io.InputStream
import javax.inject.Inject

class BuildingRepositoryImpl
    @Inject
    constructor(
        private val buildingLocalDatasource: BuildingLocalDataSource,
    ) : BuildingRepository {
        override suspend fun getBuildings(inputStream: InputStream): Array<Building> {
            val buildingsDataModel = buildingLocalDatasource.getBuildingsDataFromJson(inputStream)
            return buildingsDataModel.buildingsData
        }
    }
