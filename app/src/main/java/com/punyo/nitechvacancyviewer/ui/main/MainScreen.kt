package com.punyo.nitechvacancyviewer.ui.main

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.buildingvacancy.BuildingVacancyScreen
import com.punyo.nitechvacancyviewer.ui.component.LoadingProgressIndicatorComponent
import com.punyo.nitechvacancyviewer.ui.navigation.ScreenDestinations
import com.punyo.nitechvacancyviewer.ui.roomreservation.RoomReservationScreen
import com.punyo.nitechvacancyviewer.ui.setting.SettingScreen
import kotlinx.coroutines.delay

@Composable
fun MainScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    mainScreenViewModel: MainScreenViewModel = hiltViewModel(),
) {
    val currentState by mainScreenViewModel.uiState.collectAsStateWithLifecycle()
    val navbarLabels =
        listOf(
            stringResource(id = R.string.UI_NAVIGATIONBARITEM_TEXT_HOME),
            stringResource(id = R.string.UI_NAVIGATIONBARITEM_TEXT_RESERVATION),
            stringResource(id = R.string.UI_NAVIGATIONBARITEM_TEXT_SETTINGS),
        )
    val navbarIcons =
        listOf(
            Icons.Default.Home,
            Icons.AutoMirrored.Filled.List,
            Icons.Default.Settings,
        )
    val vacancyUpdateInterval =
        integerResource(id = R.integer.VACANCY_UPDATE_INTERVAL_MILLISECOND).toLong()
    val pullToRefreshDelay =
        integerResource(id = R.integer.PULL_TO_REFRESH_DELAY_MILLISECOND).toLong()

    LaunchedEffect(key1 = Unit) {
        while (true) {
            mainScreenViewModel.updateVacancy()
            delay(vacancyUpdateInterval)
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MainScreenAppBar(currentNavTitle = navbarLabels[currentState.currentNavIndex]) },
        bottomBar = {
            NavigationBar {
                navbarLabels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        icon = { Icon(navbarIcons[index], contentDescription = null) },
                        label = { Text(label) },
                        selected = index == currentState.currentNavIndex,
                        onClick = {
                            mainScreenViewModel.onNavItemClick(index)
                        },
                    )
                }
            }
        },
    ) { innerPadding ->
        if (currentState.roomsData != null) {
            when (currentState.currentNavIndex) {
                0 ->
                    BuildingVacancyScreen(
                        modifier = modifier.padding(innerPadding),
                        navHostController = navHostController,
                        onRefreshVacancy = { mainScreenViewModel.onRefreshVacancy(pullToRefreshDelay) },
                        isRefreshVacancy = currentState.isRefreshVacancy,
                        lastVacancyRefreshTimeString = mainScreenViewModel.getLastUpdateTimeString(),
                        roomsData = currentState.roomsData!!,
                    )

                1 ->
                    RoomReservationScreen(
                        modifier = modifier.padding(innerPadding),
                        navHostController = navHostController,
                        roomsData = currentState.roomsData!!,
                    )

                2 ->
                    SettingScreen(
                        modifier = modifier.padding(innerPadding),
                        navHostController = navHostController,
                    )
            }
        } else {
            LoadingProgressIndicatorComponent()
        }
        if (currentState.isTodayRoomsDataNotFoundOnDB) {
            navHostController.navigate(ScreenDestinations.Initialize.name)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenAppBar(
    modifier: Modifier = Modifier,
    currentNavTitle: String,
) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = currentNavTitle) },
        colors =
        TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        ),
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun LightModePreview() {
    AppTheme {
        MainScreen(navHostController = NavHostController(LocalContext.current))
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkModePreview() {
    AppTheme {
        MainScreen(navHostController = NavHostController(LocalContext.current))
    }
}
