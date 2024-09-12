package com.punyo.nitechroomvacancyviewer.data.building.model

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

data class Building(
    val buildingNameResourceName: String,
    val buildingImageResourceName: String,
    val buildingRoomDisplayNames: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Building

        if (buildingNameResourceName != other.buildingNameResourceName) return false
        if (!buildingRoomDisplayNames.contentEquals(other.buildingRoomDisplayNames)) return false
        if (buildingImageResourceName != other.buildingImageResourceName) return false

        return true
    }

    override fun hashCode(): Int {
        var result = buildingNameResourceName.hashCode()
        result = 31 * result + buildingRoomDisplayNames.contentHashCode()
        result = 31 * result + buildingImageResourceName.hashCode()
        return result
    }

}
