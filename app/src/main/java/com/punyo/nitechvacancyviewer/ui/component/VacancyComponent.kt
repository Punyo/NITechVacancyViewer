package com.punyo.nitechvacancyviewer.ui.component

import android.annotation.SuppressLint
import android.app.Application
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.punyo.nitechvacancyviewer.GsonInstance
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechvacancyviewer.data.building.source.BuildingLocalDatasource
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.ui.ScreenDestinations
import com.punyo.nitechvacancyviewer.ui.model.VacancyComponentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("DiscouragedApi")
@Composable
fun VacancyComponent(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    viewModel: VacancyComponentViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
        factory = VacancyComponentViewModel.Factory(
            LocalContext.current.applicationContext as Application,
            BuildingRepository(BuildingLocalDatasource()), RoomRepository()
        )
    )
) {
    val context = LocalContext.current
    val currentState by viewModel.uiState.collectAsStateWithLifecycle()
    val isAllDataLoaded = currentState.buildings != null && currentState.roomsData != null
    val navigationRoute = stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN)
    val navigationRouteParam1 =
        stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER1)
    val navigationRoteParam2 =
        stringResource(id = R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER2)
    val vacancyUpdateInterval =
        integerResource(id = R.integer.VACANCY_UPDATE_INTERVAL_MILLISECOND).toLong()
    val pullToRefreshDelay =
        integerResource(id = R.integer.PULL_TO_REFRESH_DELAY_MILLISECOND).toLong()
    val coroutineScope = rememberCoroutineScope()
    val pullToRefreshState = rememberPullToRefreshState()
    LaunchedEffect(key1 = Unit) {
        viewModel.loadBuildings(context.resources.openRawResource(R.raw.buildings))
        viewModel.updateRoomVacancy()
    }
    LaunchedEffect(key1 = currentState.roomsData) {
        while (true) {
            viewModel.updateRoomVacancy()
            delay(vacancyUpdateInterval)
        }
    }
    if (isAllDataLoaded) {
        val buildings = currentState.buildings!!
        val rooms = currentState.roomsData!!
        PullToRefreshBox(
            modifier = modifier.padding(8.dp),
            state = pullToRefreshState,
            isRefreshing = currentState.isRefreshing,
            onRefresh = {
                viewModel.setRefreshing(true)
                coroutineScope.launch {
                    viewModel.updateRoomVacancy()
                    delay(pullToRefreshDelay)
                    viewModel.setRefreshing(false)
                }
            }
        ) {
            LazyVerticalGrid(
                modifier = Modifier.fillMaxSize(), columns = GridCells.Fixed(2)
            ) {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    Text(
                        modifier = Modifier.padding(8.dp),
                        text = stringResource(id = R.string.UI_LAZYVERTICALGRID_TEXT_LASTUPDATETIME).format(
                            currentState.lastUpdateTime?.let { viewModel.getLastUpdateTimeString(it) }
                        ),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                items(buildings.size) { index ->
                    val buildingData = buildings[index]
                    val buildingNameIdentifier = context.resources.getIdentifier(
                        buildingData.buildingNameResourceName,
                        "string",
                        context.packageName
                    )
                    val roomsData = rooms.filter { room ->
                        buildingData.buildingRoomDisplayNames.contains(room.roomDisplayName)
                    }.toTypedArray()
                    val numberOfVacantRooms =
                        viewModel.getNumberOfVacantRoom(roomsData, LocalDateTime.now())
                    BuildingsCard(
                        modifier = Modifier.padding(8.dp),
                        buildingName = buildingNameIdentifier,
                        buildingImage = context.resources.getIdentifier(
                            buildingData.buildingImageResourceName,
                            "drawable",
                            context.packageName
                        ),
                        numberOfVacantRooms = numberOfVacantRooms,
                        numberOfRooms = buildingData.buildingRoomDisplayNames.size.toUInt(),
                        onClick = {
                            navHostController.navigate(
                                navigationRoute.replace(
                                    "{${navigationRouteParam1}}",
                                    context.getString(buildingNameIdentifier)
                                ).replace(
                                    "{${navigationRoteParam2}}",
                                    GsonInstance.gson.toJson(roomsData)
                                )
                            )
                        }
                    )
                }
            }
        }
    } else {
        LoadingProgressIndicatorComponent()
    }
    if (currentState.isTodayRoomsDataNotFoundOnDB) {
        navHostController.navigate(ScreenDestinations.Initialize.name)
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