package com.punyo.nitechroomvacancyviewer.data.building.model

import kotlinx.serialization.Serializable

@Serializable
data class BuildingsDataModel(
    val buildingsData: Array<Building>
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingsDataModel

        return buildingsData.contentEquals(other.buildingsData)
    }

    override fun hashCode(): Int {
        return buildingsData.contentHashCode()
    }

}

@Serializable
data class Building(
    val buildingNameResourceName: String,
    val buildingImageResourceName: String,
    val buildingRoomPrincipalNames: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (buildingNameResourceName != other.buildingNameResourceName) return false
        if (!buildingRoomPrincipalNames.contentEquals(other.buildingRoomPrincipalNames)) return false
        if (buildingImageResourceName != other.buildingImageResourceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buildingNameResourceName.hashCode()
        result = 31 * result + buildingRoomPrincipalNames.contentHashCode()
        result = 31 * result + buildingImageResourceName.hashCode()
        return result
    }

}
