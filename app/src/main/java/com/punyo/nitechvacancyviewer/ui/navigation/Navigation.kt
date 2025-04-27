package com.punyo.nitechvacancyviewer.ui.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.application.GsonInstance
import com.punyo.nitechvacancyviewer.data.room.model.Room
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.component.ReservationTableScreen
import com.punyo.nitechvacancyviewer.ui.initialize.InitializeScreen
import com.punyo.nitechvacancyviewer.ui.main.MainScreen
import com.punyo.nitechvacancyviewer.ui.roomvacancy.RoomVacancyScreen
import com.punyo.nitechvacancyviewer.ui.signin.SignInScreen

@Composable
fun MainNavigation(
    navController: NavHostController = rememberNavController(),
    navigationViewModel: MainNavigationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val themeSettings by navigationViewModel.currentTheme.collectAsStateWithLifecycle()
    val isDarkTheme =
        when (themeSettings) {
            ThemeSettings.LIGHT -> false
            ThemeSettings.DARK -> true
            else -> isSystemInDarkTheme()
        }

    AppTheme(isDarkTheme) {
        NavHost(
            modifier = Modifier.fillMaxSize(),
            navController = navController,
            startDestination = ScreenDestinations.Initialize.name,
        ) {
            composable(ScreenDestinations.Initialize.name) {
                InitializeScreen(
                    onLoadedRoomsData = {
                        navigateOneSide(
                            navController,
                            ScreenDestinations.Initialize,
                            ScreenDestinations.Main,
                        )
                    },
                    onFailedSignInWithSavedCredentials = {
                        navigateOneSide(
                            navController,
                            ScreenDestinations.Initialize,
                            ScreenDestinations.SignIn,
                        )
                    },
                )
            }
            composable(ScreenDestinations.SignIn.name) {
                SignInScreen(onSignInSuccess = {
                    navigateOneSide(
                        navController,
                        ScreenDestinations.SignIn,
                        ScreenDestinations.Initialize,
                    )
                })
            }
            composable(ScreenDestinations.Main.name) {
                MainScreen(navHostController = navController)
            }
            composable(
                route = context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN),
                arguments =
                    listOf(
                        navArgument(context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER1)) {
                            type = NavType.StringType
                        },
                        navArgument(context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER2)) {
                            type = NavType.StringType
                        },
                    ),
            ) { backStackEntry ->
                val buildingName =
                    backStackEntry.arguments?.getString(context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER1))
                        ?: ""
                val roomVacancy =
                    backStackEntry.arguments?.getString(context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER2))
                        ?: ""

                RoomVacancyScreen(
                    buildingName = buildingName,
                    onBackPressed = { navController.popBackStack() },
                    rooms = GsonInstance.gson.fromJson(roomVacancy, Array<Room>::class.java),
                )
            }
            composable(
                route = context.getString(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN),
                arguments =
                    listOf(
                        navArgument(context.getString(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN_PARAMETER1)) {
                            type = NavType.StringType
                        },
                    ),
            ) { backStackEntry ->
                val roomData =
                    backStackEntry.arguments?.getString(context.getString(R.string.UI_NAVHOST_COMPOSABLE_RESERVATIONTABLESCREEN_PARAMETER1))

                ReservationTableScreen(
                    roomData = GsonInstance.gson.fromJson(roomData, Room::class.java),
                    onBackPressed = { navController.popBackStack() },
                )
            }
        }
    }
}

private fun navigateOneSide(
    navController: NavHostController,
    from: ScreenDestinations,
    to: ScreenDestinations,
) {
    navController.navigate(to.name) {
        popUpTo(from.name) { inclusive = true }
    }
}

enum class ScreenDestinations {
    Initialize,
    SignIn,
    Main,
}
