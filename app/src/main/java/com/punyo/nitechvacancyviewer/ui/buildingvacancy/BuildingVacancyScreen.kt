package com.punyo.nitechvacancyviewer.ui.buildingvacancy

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.application.GsonInstance
import com.punyo.nitechvacancyviewer.data.building.model.Building
import com.punyo.nitechvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.component.LastUpdateTimeTextComponent
import com.punyo.nitechvacancyviewer.ui.component.LoadingProgressIndicatorComponent
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
fun BuildingVacancyScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    onRefreshVacancy: () -> Unit,
    isRefreshVacancy: Boolean,
    lastVacancyRefreshTimeString: String,
    roomsData: Array<Room>,
    viewModel: VacancyComponentViewModel = hiltViewModel(),
) {
    val resources = LocalResources.current
    val currentState by viewModel.uiState.collectAsStateWithLifecycle()
    val navigationRoute = stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN)
    val navigationRouteParam1 =
        stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER1)
    val navigationRoteParam2 =
        stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER2)

    // 副作用: buildings読み込み
    LaunchedEffect(key1 = Unit) {
        viewModel.loadBuildings(resources.openRawResource(R.raw.buildings))
    }

    BuildingVacancyScreenInternal(
        modifier = modifier,
        state = currentState,
        roomsData = roomsData,
        isRefreshVacancy = isRefreshVacancy,
        lastVacancyRefreshTimeString = lastVacancyRefreshTimeString,
        currentTime = LocalDateTime.now(),
        onRefreshVacancy = onRefreshVacancy,
        onBuildingClick = { buildingName, rooms ->
            navHostController.navigate(
                navigationRoute
                    .replace("{$navigationRouteParam1}", buildingName)
                    .replace("{$navigationRoteParam2}", GsonInstance.gson.toJson(rooms)),
            )
        },
        getNumberOfVacantRoom = { rooms, time ->
            viewModel.getNumberOfVacantRoom(rooms, time)
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
private fun BuildingVacancyScreenInternal(
    modifier: Modifier = Modifier,
    state: VacancyComponentUiState,
    roomsData: Array<Room>,
    isRefreshVacancy: Boolean,
    lastVacancyRefreshTimeString: String,
    currentTime: LocalDateTime,
    onRefreshVacancy: () -> Unit = {},
    onBuildingClick: (String, Array<Room>) -> Unit = { _, _ -> },
    getNumberOfVacantRoom: (Array<Room>, LocalDateTime) -> UInt = { _, _ -> 0u },
) {
    val context = LocalContext.current
    val resources = LocalResources.current
    val pullToRefreshState = rememberPullToRefreshState()

    if (state.buildings != null) {
        val buildings = state.buildings
        PullToRefreshBox(
            modifier = modifier.padding(start = 8.dp, end = 8.dp),
            state = pullToRefreshState,
            isRefreshing = isRefreshVacancy,
            onRefresh = onRefreshVacancy,
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(),
                columns = GridCells.Fixed(2),
            ) {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    LastUpdateTimeTextComponent(lastUpdateTimeString = lastVacancyRefreshTimeString)
                }
                items(buildings.size) { index ->
                    val buildingData = buildings[index]
                    val buildingNameIdentifier =
                        resources.getIdentifier(
                            buildingData.buildingNameResourceName,
                            "string",
                            context.packageName,
                        )
                    val registeredRoomsData =
                        roomsData
                            .filter { room ->
                                buildingData.buildingRoomDisplayNames.contains(room.roomDisplayName)
                            }.toTypedArray()
                    val numberOfVacantRooms =
                        getNumberOfVacantRoom(registeredRoomsData, currentTime)
                    BuildingsCard(
                        modifier = Modifier.padding(8.dp),
                        buildingName = buildingNameIdentifier,
                        buildingImage =
                            resources.getIdentifier(
                                buildingData.buildingImageResourceName,
                                "drawable",
                                context.packageName,
                            ),
                        numberOfVacantRooms = numberOfVacantRooms,
                        numberOfRooms = buildingData.buildingRoomDisplayNames.size.toUInt(),
                        onClick = {
                            onBuildingClick(
                                resources.getString(buildingNameIdentifier),
                                registeredRoomsData,
                            )
                        },
                    )
                }
            }
        }
    } else {
        LoadingProgressIndicatorComponent()
    }
}

@Composable
fun BuildingsCard(
    modifier: Modifier = Modifier,
    @StringRes buildingName: Int,
    @DrawableRes buildingImage: Int,
    numberOfVacantRooms: UInt,
    numberOfRooms: UInt,
    onClick: () -> Unit,
) {
    ElevatedCard(
        modifier = modifier.clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp),
    ) {
        Image(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .clip(MaterialTheme.shapes.medium),
            painter = painterResource(id = buildingImage),
            contentScale = ContentScale.Crop,
            contentDescription = stringResource(id = buildingName),
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 8.dp),
            text = stringResource(id = buildingName),
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurface,
            maxLines = 1,
        )
        Text(
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, bottom = 8.dp),
            text =
                stringResource(id = R.string.UI_CARD_TEXT_VACANTANDMAXROOMS).format(
                    numberOfVacantRooms.toInt(),
                    numberOfRooms.toInt(),
                ),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun BuildingVacancyScreenLightPreview() {
    val currentTime = LocalDateTime.now()
    val rooms =
        arrayOf(
            Room(
                "講義室A",
                arrayOf(
                    EventInfo(currentTime.minusHours(1), currentTime.plusHours(1), "授業1"),
                ),
            ),
            Room(
                "講義室B",
                arrayOf(
                    EventInfo(currentTime.plusHours(1), currentTime.plusHours(2), "授業2"),
                ),
            ),
        )
    val building1 = Building(
        "BUILDING_1",
        "thumbnail_building1",
        arrayOf("講義室A", "講義室B"),
    )

    val buildings =
        arrayOf(
            building1,
            building1,
        )
    AppTheme {
        BuildingVacancyScreenInternal(
            state = VacancyComponentUiState(buildings = buildings),
            roomsData = rooms,
            isRefreshVacancy = false,
            lastVacancyRefreshTimeString = "2024-01-01 12:00",
            currentTime = currentTime,
            getNumberOfVacantRoom = { roomsData, time ->
                roomsData
                    .filter { room ->
                        !room.eventsInfo.any { eventTime ->
                            time.isAfter(eventTime.start) && time.isBefore(eventTime.end)
                        }
                    }.size
                    .toUInt()
            },
        )
    }
}
