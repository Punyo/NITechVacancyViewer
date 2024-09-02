package com.punyo.nitechroomvacancyviewer.ui.screen

import androidx.compose.foundation.gestures.rememberScrollableState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.ui.component.RoomVacancy
import com.punyo.nitechroomvacancyviewer.ui.component.RoomVacancyStatus
import com.punyo.nitechroomvacancyviewer.ui.component.TopAppBarWithBackArrowComponent
import com.punyo.nitechroomvacancyviewer.ui.theme.AppTheme

@Composable
fun RoomVacancyScreen(
    buildingName: String,
    onBackPressed: () -> Unit = {},
    roomsVacancy: Array<RoomVacancy>
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithBackArrowComponent(
                headerText = buildingName,
                onBackPressed = onBackPressed
            )
        }) { innerPadding ->
        LazyColumn(modifier = Modifier.padding(innerPadding)) {
            items(roomsVacancy.size) { index ->
                val roomVacancy = roomsVacancy[index]
                ListItem(
                    headlineContent = {
                        Text(
                            text = roomVacancy.roomName,
                        )
                    },
                    leadingContent = {
                        when (roomVacancy.vacancyStatus) {
                            RoomVacancyStatus.VACANT -> {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = stringResource(id = R.string.UI_LISTITEM_TEXT_VACANT),
                                    tint = MaterialTheme.colorScheme.primaryContainer
                                )
                            }

                            RoomVacancyStatus.OCCUPY -> {
                                Icon(
                                    painter = painterResource(id = R.drawable.outline_cancel_24),
                                    contentDescription = stringResource(id = R.string.UI_LISTITEM_TEXT_OCCUPY),
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        }
                    },
                    supportingContent = {
                        Text(
                            text = when (roomVacancy.vacancyStatus) {
                                RoomVacancyStatus.VACANT -> stringResource(id = R.string.UI_LISTITEM_TEXT_VACANT)
                                RoomVacancyStatus.OCCUPY -> stringResource(id = R.string.UI_LISTITEM_TEXT_OCCUPY)
                            }
                        )
                    }
                )
                if (index != roomsVacancy.size - 1) HorizontalDivider()
            }
            item { HorizontalDivider() }
        }
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun RoomVacancyScreenLightModePreview() {
    AppTheme {
        RoomVacancyScreen(
            "建物の名称", roomsVacancy = arrayOf(
                RoomVacancy("部屋1", RoomVacancyStatus.VACANT),
                RoomVacancy("部屋2", RoomVacancyStatus.OCCUPY)
            )
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun RoomVacancyScreenDarkModePreview() {
    AppTheme {
        RoomVacancyScreen(
            "建物の名称", roomsVacancy = arrayOf(
                RoomVacancy("部屋1", RoomVacancyStatus.VACANT),
                RoomVacancy("部屋2", RoomVacancyStatus.OCCUPY)
            )
        )
    }
}