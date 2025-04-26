package com.punyo.nitechvacancyviewer.data.building

import com.punyo.nitechvacancyviewer.data.building.model.Building
import java.io.InputStream

interface BuildingRepository {
    suspend fun getBuildings(inputStream: InputStream): Array<Building>
}
