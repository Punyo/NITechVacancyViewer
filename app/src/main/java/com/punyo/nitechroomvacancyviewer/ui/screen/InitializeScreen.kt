package com.punyo.nitechroomvacancyviewer.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microsoft.identity.client.exception.MsalException
import com.punyo.nitechroomvacancyviewer.data.MSGraphRepository
import com.punyo.nitechroomvacancyviewer.ui.model.InitializeScreenViewModel

@Composable
fun InitializeScreen(
    onAlreadySignedIn: () -> Unit,
    onNotSignedIn: () -> Unit,
    onInitializeFailed: (MsalException) -> Unit,
    initializeScreenViewModel: InitializeScreenViewModel = InitializeScreenViewModel(
        MSGraphRepository()
    )
) {
    val currentState = initializeScreenViewModel.uiState.collectAsStateWithLifecycle().value
    initializeScreenViewModel.initMSAL(LocalContext.current)
    if (currentState.alreadySignedIn == null && currentState.thrownException == null) {

    } else {
        if (currentState.thrownException != null) {
            onInitializeFailed(currentState.thrownException)
        }
        if (currentState.alreadySignedIn == true) {
            onAlreadySignedIn()
        } else if (currentState.alreadySignedIn == false) {
            onNotSignedIn()
        }
    }
}