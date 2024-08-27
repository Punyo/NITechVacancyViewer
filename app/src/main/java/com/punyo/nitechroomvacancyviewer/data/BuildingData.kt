package com.punyo.nitechroomvacancyviewer.data

data class BuildingData(
    val buildingNameResourceName: String,
    val buildingImageResourceName: String,
    val buildingRoomPrincipalNames: Array<String>,
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BuildingData

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
