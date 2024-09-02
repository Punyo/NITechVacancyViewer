package com.punyo.nitechroomvacancyviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.ui.screen.InitializeScreen
import com.punyo.nitechroomvacancyviewer.ui.screen.MainScreen
import com.punyo.nitechroomvacancyviewer.ui.screen.RoomVacancyScreen
import com.punyo.nitechroomvacancyviewer.ui.screen.SignInScreen
import kotlinx.serialization.json.Json

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController()) {
    val context = LocalContext.current
    NavHost(
        modifier = Modifier.fillMaxSize(),
        navController = navController,
        startDestination = ScreenDestinations.Initialize.name
    ) {
        composable(ScreenDestinations.Initialize.name) {
            InitializeScreen(
                onAlreadySignedIn = {
                    navigateOneSide(
                        navController,
                        ScreenDestinations.Initialize,
                        ScreenDestinations.Main
                    )
                },
                onNotSignedIn = {
                    navigateOneSide(
                        navController,
                        ScreenDestinations.Initialize,
                        ScreenDestinations.SignIn
                    )
                },
                onInitializeFailed = {
                    navigateOneSide(
                        navController,
                        ScreenDestinations.Initialize,
                        ScreenDestinations.SignInInitializeFailed
                    )
                }
            )
        }
        composable(ScreenDestinations.SignIn.name) {
            SignInScreen(isInitializeSuccess = true, onSignInSuccess = {
                navigateOneSide(navController, ScreenDestinations.SignIn, ScreenDestinations.Main)
            })
        }
        composable(ScreenDestinations.Main.name) {
            MainScreen(navHostController = navController)
        }
        composable(ScreenDestinations.SignInInitializeFailed.name) {
            SignInScreen(isInitializeSuccess = false)
        }
        composable(
            route = context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN),
            arguments = listOf(
                navArgument(context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER1)) {
                    type = NavType.StringType
                },
                navArgument(context.getString(R.string.UI_NAVHOST_COMPOSABLE_ROOMVACANCYSCREEN_PARAMETER2)) {
                    type = NavType.StringType
                })
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
                roomsVacancy = Json.decodeFromString(roomVacancy)
            )
        }
    }
}

private fun navigateOneSide(
    navController: NavHostController,
    from: ScreenDestinations,
    to: ScreenDestinations
) {
    navController.navigate(to.name) {
        popUpTo(from.name) { inclusive = true }
    }
}

enum class ScreenDestinations {
    Initialize,
    SignIn,
    SignInInitializeFailed,
    Main
}