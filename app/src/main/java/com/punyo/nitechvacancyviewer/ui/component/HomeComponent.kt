package com.punyo.nitechvacancyviewer.ui.component

import android.app.Application
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.ui.ScreenDestinations
import com.punyo.nitechvacancyviewer.ui.model.HomeComponentViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeComponent(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    homeComponentViewModel: HomeComponentViewModel = viewModel(
        factory = HomeComponentViewModel.Factory(
            application = LocalContext.current.applicationContext as Application,
            roomRepository = RoomRepository()
        )
    )
) {
    val titles = arrayOf(
        stringResource(id = R.string.UI_TAB_TITLE_CURRENT_VACANT_ROOMS),
        stringResource(id = R.string.UI_TAB_TITLE_TODAY_RESERVATION_LIST)
    )
    val currentState by homeComponentViewModel.uiState.collectAsStateWithLifecycle()
    val coroutineScope = rememberCoroutineScope()
    val refreshingDelay = integerResource(id = R.integer.PULL_TO_REFRESH_DELAY_MILLISECOND).toLong()
    homeComponentViewModel.updateCurrentVacantRoomsArray()
    Column(modifier = modifier) {
        PrimaryTabRow(selectedTabIndex = currentState.selectedTabIndex) {
            titles.forEachIndexed { index, title ->
                Tab(
                    selected = currentState.selectedTabIndex == index,
                    onClick = { homeComponentViewModel.onTabSelected(index) },
                    text = { Text(text = title, maxLines = 2) }
                )
            }
        }
        when (currentState.selectedTabIndex) {
            0 -> {
                currentState.currentVacantRooms?.let {
                    CurrentVacantRoomsListComponent(
                        modifier = Modifier.weight(1f),
                        isRefreshing = currentState.isRefreshingCurrentVacantRoomsList,
                        onRefresh = {
                            homeComponentViewModel.setRefreshingCurrentVacantRoomsList(true)
                            coroutineScope.launch {
                                homeComponentViewModel.updateCurrentVacantRoomsArray()
                                delay(refreshingDelay)
                                homeComponentViewModel.setRefreshingCurrentVacantRoomsList(false)
                            }
                        },
                        vacantRoomsAndMinutesUntilNextEvent = it.associateWith { room ->
                            homeComponentViewModel.getMinutesUntilNextEvent(
                                room
                            )
                        })
                }
            }

            1 -> {

            }
        }
    }
    if (currentState.isTodayRoomsDataNotFoundOnDB) {
        navHostController.navigate(ScreenDestinations.Initialize.name)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TodayReservationListComponent(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit
) {
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {

    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrentVacantRoomsListComponent(
    modifier: Modifier = Modifier,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    vacantRoomsAndMinutesUntilNextEvent: Map<Room, Int?>
) {
    PullToRefreshBox(
        modifier = modifier.fillMaxSize(),
        isRefreshing = isRefreshing,
        onRefresh = onRefresh
    ) {
        LazyColumn {
            items(vacantRoomsAndMinutesUntilNextEvent.size) { index ->
                val room = vacantRoomsAndMinutesUntilNextEvent.keys.elementAt(index)
                ListItem(
                    headlineContent = { Text(text = room.roomDisplayName) },
                    supportingContent = {
                        if (vacantRoomsAndMinutesUntilNextEvent[room] == null) {
                            Text(text = stringResource(id = R.string.UI_LISTITEM_TEXT_NO_NEXT_EVENT))
                        } else {
                            Text(
                                text = stringResource(id = R.string.UI_LISTITEM_TEXT_MINUTESUNTILNEXTEVENT).format(
                                    vacantRoomsAndMinutesUntilNextEvent[room]
                                )
                            )
                        }
                    },
                )
                if (index != vacantRoomsAndMinutesUntilNextEvent.size - 1) HorizontalDivider()
            }
            item { HorizontalDivider() }
        }
    }
}