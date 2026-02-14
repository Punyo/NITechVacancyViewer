package com.punyo.nitechvacancyviewer.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.room.model.Room

@Composable
fun ReservationTableScreen(
    roomData: Room,
    onBackPressed: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBarWithBackArrowComponent(
                headerText = roomData.roomDisplayName,
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
            val events = roomData.eventsInfo
            events.forEach { event ->
                ListItem(
                    headlineContent = { Text(text = event.eventDescription) },
                    supportingContent = { Text(text = event.getStartAndEndString()) },
                )
                HorizontalDivider()
            }
            if (events.isEmpty()) {
                Box(
                    modifier =
                    Modifier
                        .fillMaxSize()
                        .align(Alignment.CenterHorizontally),
                ) {
                    Text(
                        modifier = Modifier.align(Alignment.Center),
                        textAlign = TextAlign.Center,
                        text = stringResource(R.string.UI_TEXT_NO_RESERVATION),
                    )
                }
            }
        }
    }
}
