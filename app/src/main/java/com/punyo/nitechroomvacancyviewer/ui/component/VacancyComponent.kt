package com.punyo.nitechroomvacancyviewer.ui.component

import android.annotation.SuppressLint
import android.os.Parcelable
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.versionedparcelable.ParcelField
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechroomvacancyviewer.data.building.source.BuildingLocalDatasource
import com.punyo.nitechroomvacancyviewer.ui.model.VacancyComponentViewModel
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

@SuppressLint("DiscouragedApi")
@Composable
fun VacancyComponent(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: VacancyComponentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = VacancyComponentViewModel.Factory(
            BuildingRepository(
                BuildingLocalDatasource()
            )
        )
    )
) {
    val context = LocalContext.current
    val currentState by viewModel.uiState.collectAsStateWithLifecycle()

    val navigationRoute = stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN)
    val navigationRouteParam1 =
        stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER1)
    val navigationRoteParam2 =
        stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER2)
    LaunchedEffect(true) {
        viewModel.loadBuildings(context.resources.openRawResource(R.raw.buildings))
    }
    if (currentState.buildings == null) {
        LoadingProgressIndicatorComponent()
    }
    currentState.buildings?.let { buildings ->
        LazyVerticalGrid(modifier = modifier.padding(8.dp), columns = GridCells.Fixed(2)) {
            items(buildings.size) { index ->
                val buildingData = buildings[index]
                val numberOfVacantRooms =
                    viewModel.getNumberOfVacantRoom(buildingData.buildingRoomPrincipalNames)
                val buildingNameIdentifier = context.resources.getIdentifier(
                    buildingData.buildingNameResourceName,
                    "string",
                    context.packageName
                )
                val roomsVacancy = buildingData.buildingRoomPrincipalNames.map {
                    RoomVacancy(
                        roomName = it,
                        vacancyStatus = RoomVacancyStatus.OCCUPY
                    )
                }.toTypedArray()
                BuildingsCard(
                    modifier = Modifier.padding(8.dp),
                    buildingName = buildingNameIdentifier,
                    buildingImage = context.resources.getIdentifier(
                        buildingData.buildingImageResourceName,
                        "drawable",
                        context.packageName
                    ),
                    numberOfVacantRooms = numberOfVacantRooms,
                    numberOfRooms = buildingData.buildingRoomPrincipalNames.size.toUInt(),
                    onClick = {
                        navHostController.navigate(
                            navigationRoute.replace(
                                "{${navigationRouteParam1}}",
                                context.getString(buildingNameIdentifier)
                            ).replace(
                                "{${navigationRoteParam2}}",
                                Json.encodeToString(roomsVacancy)
                            )
                        )
                    }
                )
            }
        }
    }

}

@Composable
fun BuildingsCard(
    modifier: Modifier = Modifier,
    @StringRes buildingName: Int,
    @DrawableRes buildingImage: Int,
    numberOfVacantRooms: UInt,
    numberOfRooms: UInt,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Image(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .clip(MaterialTheme.shapes.medium),
            painter = painterResource(id = buildingImage),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = buildingName)
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = stringResource(id = buildingName),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            text = stringResource(id = R.string.UI_CARD_TEXT_VACANTANDMAXROOMS).format(
                numberOfVacantRooms.toInt(),
                numberOfRooms.toInt()
            ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Serializable
data class RoomVacancy(
    val roomName: String,
    val vacancyStatus: RoomVacancyStatus
)

@Serializable
enum class RoomVacancyStatus {
    VACANT,
    OCCUPY
}