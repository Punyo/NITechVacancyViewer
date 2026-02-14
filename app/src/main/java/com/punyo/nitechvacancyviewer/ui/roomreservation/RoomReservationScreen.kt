package com.punyo.nitechvacancyviewer.ui.roomreservation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.application.GsonInstance
import com.punyo.nitechvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.theme.AppTheme
import java.time.LocalDateTime

@Composable
fun RoomReservationScreen(
    modifier: Modifier = Modifier,
    roomsData: Array<Room>,
    navHostController: NavHostController,
) {
    val navigationRoute = stringResource(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN)
    val navigationParam1 =
        stringResource(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN_PARAMETER1)

    RoomReservationScreenInternal(
        modifier = modifier,
        roomsData = roomsData,
        onRoomClick = { room ->
            navHostController.navigate(
                navigationRoute.replace(
                    "{$navigationParam1}",
                    GsonInstance.gson.toJson(room),
                ),
            )
        },
    )
}

@Composable
private fun RoomReservationScreenInternal(
    modifier: Modifier = Modifier,
    roomsData: Array<Room>,
    onRoomClick: (Room) -> Unit = {},
) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(roomsData.size) { index ->
            val room = roomsData[index]
            val eventsInfo = room.eventsInfo
            ListItem(
                modifier = Modifier.clickable { onRoomClick(room) },
                headlineContent = { Text(text = room.roomDisplayName) },
                supportingContent = {
                    if (eventsInfo.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.UI_LISTITEM_TEXT_RESERVATION_NUM).format(eventsInfo.size),
                        )
                    } else {
                        Text(text = stringResource(R.string.UI_LISTITEM_TEXT_NO_RESERVATION))
                    }
                },
            )
            if (index != roomsData.size - 1) HorizontalDivider()
        }
        item { HorizontalDivider() }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun RoomReservationScreen() {
    val currentTime = LocalDateTime.now()
    val rooms =
        arrayOf(
            Room(
                "講義室A",
                arrayOf(
                    EventInfo(
                        currentTime.plusHours(1),
                        currentTime.plusHours(2),
                        "イベント1",
                    ),
                    EventInfo(
                        currentTime.plusHours(3),
                        currentTime.plusHours(4),
                        "イベント2",
                    ),
                ),
            ),
        )
    AppTheme {
        RoomReservationScreenInternal(
            roomsData = rooms,
        )
    }
}
