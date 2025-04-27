package com.punyo.nitechvacancyviewer.ui.roomvacancy

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.application.enums.RoomVacancyStatus
import com.punyo.nitechvacancyviewer.data.room.model.EventInfo
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.component.TopAppBarWithBackArrowComponent
import java.time.LocalDateTime

@Composable
fun RoomVacancyScreen(
    buildingName: String,
    onBackPressed: () -> Unit = {},
    rooms: Array<Room>,
    roomVacancyScreenViewModel: RoomVacancyScreenViewModel = viewModel(),
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithBackArrowComponent(
                headerText = buildingName,
                onBackPressed = onBackPressed,
            )
        },
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState()),
        ) {
            rooms.forEachIndexed { index, room ->
                val roomVacancyStatus =
                    roomVacancyScreenViewModel.getRoomVacancy(room, LocalDateTime.now())
                ListItem(
                    headlineContent = {
                        Text(
                            text = room.roomDisplayName,
                        )
                    },
                    leadingContent = {
                        when (roomVacancyStatus) {
                            RoomVacancyStatus.VACANT -> {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = stringResource(id = R.string.UI_LISTITEM_TEXT_VACANT),
                                    tint = MaterialTheme.colorScheme.primaryContainer,
                                )
                            }

                            RoomVacancyStatus.OCCUPY -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_cancel_24),
                                    contentDescription = stringResource(id = R.string.UI_LISTITEM_TEXT_OCCUPY),
                                    tint = MaterialTheme.colorScheme.error,
                                )
                            }
                        }
                    },
                    supportingContent = {
                        Text(
                            text =
                                when (roomVacancyStatus) {
                                    RoomVacancyStatus.VACANT -> stringResource(id = R.string.UI_LISTITEM_TEXT_VACANT)
                                    RoomVacancyStatus.OCCUPY ->
                                        stringResource(id = R.string.UI_LISTITEM_TEXT_OCCUPY).format(
                                            roomVacancyScreenViewModel
                                                .getCurrentEvent(
                                                    room,
                                                    LocalDateTime.now(),
                                                )?.eventDescription ?: "",
                                        )
                                },
                        )
                    },
                )
                if (index != rooms.size - 1) HorizontalDivider()
            }
            HorizontalDivider()
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun RoomVacancyScreenLightModePreview() {
    AppTheme {
        RoomVacancyScreen(
            "建物の名称",
            rooms =
                arrayOf(
                    Room(
                        "部屋1",
                        arrayOf(
                            EventInfo(
                                LocalDateTime.now().minusHours(1),
                                LocalDateTime.now().plusHours(1),
                                "数理情報概論",
                            ),
                        ),
                    ),
                    Room(
                        "部屋2",
                        arrayOf(
                            EventInfo(
                                LocalDateTime.now().plusHours(1),
                                LocalDateTime.now().plusHours(2),
                                "イベント2",
                            ),
                        ),
                    ),
                ),
        )
    }
}
