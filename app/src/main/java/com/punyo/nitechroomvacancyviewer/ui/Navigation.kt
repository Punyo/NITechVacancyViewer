package com.punyo.nitechroomvacancyviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun MainNavigation(navController: NavHostController = rememberNavController()) {
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
            SignInScreen(isInitializeFailed = false, onSignInSuccess = {
                navigateOneSide(navController, ScreenDestinations.SignIn, ScreenDestinations.Main)
            })
        }
        composable(ScreenDestinations.Main.name) {
            MainScreen()
        }
        composable(ScreenDestinations.SignInInitializeFailed.name) {
            SignInScreen(isInitializeFailed = true)
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