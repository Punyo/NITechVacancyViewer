package com.punyo.nitechvacancyviewer.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.punyo.nitechvacancyviewer.GsonInstance
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.ui.model.ReservationRoomListComponentViewModel

@Composable
fun RoomReservationListComponent(
    modifier: Modifier = Modifier,
    roomsData: Array<Room>,
    navHostController: NavHostController,
    reservationRoomListComponentViewModel: ReservationRoomListComponentViewModel = viewModel()
) {
    val vacantRoomsAndMinutesUntilNextEvent = roomsData.associateWith { room ->
        reservationRoomListComponentViewModel.getMinutesUntilNextEvent(room)
    }
    val navigationRoute = stringResource(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN)
    val navigationParam1 =
        stringResource(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN_PARAMETER1)
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(vacantRoomsAndMinutesUntilNextEvent.size) { index ->
            val room = vacantRoomsAndMinutesUntilNextEvent.keys.elementAt(index)
            val minutesUntilNextEvent = vacantRoomsAndMinutesUntilNextEvent[room]
            ListItem(
                modifier = Modifier.clickable {
                    navHostController.navigate(
                        navigationRoute.replace(
                            "{${navigationParam1}}",
                            GsonInstance.gson.toJson(room)
                        )
                    )
                },
                headlineContent = { Text(text = room.roomDisplayName) },
                supportingContent = {
                    if (minutesUntilNextEvent != null) {
                        Text(
                            text = stringResource(R.string.UI_LISTITEM_TEXT_MINUTESUNTILNEXTEVENT).format(
                                minutesUntilNextEvent
                            )
                        )
                    } else {
                        Text(text = stringResource(R.string.UI_LISTITEM_TEXT_NO_NEXT_EVENT))
                    }
                }
            )
            if (index != vacantRoomsAndMinutesUntilNextEvent.size - 1) HorizontalDivider()
        }
        item { HorizontalDivider() }
    }

}