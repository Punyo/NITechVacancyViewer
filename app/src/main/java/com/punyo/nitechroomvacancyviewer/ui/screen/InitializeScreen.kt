package com.punyo.nitechroomvacancyviewer.ui.screen

import android.app.Application
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.nitechroomvacancyviewer.ui.component.LoadingProgressIndicatorComponent
import com.punyo.nitechroomvacancyviewer.ui.model.InitializeScreenViewModel
import com.punyo.nitechroomvacancyviewer.ui.theme.AppTheme

@Composable
fun InitializeScreen(
    onSignedInWithSavedCredentials: () -> Unit,
    onNotSignedIn: () -> Unit,
    initializeScreenViewModel: InitializeScreenViewModel = viewModel(
        factory = InitializeScreenViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val currentState by initializeScreenViewModel.uiState.collectAsStateWithLifecycle()
    LaunchedEffect(key1 = Unit) {
        initializeScreenViewModel.tryToSignInWithSavedCredentials()
    }
    LaunchedEffect(currentState.signedInWithSavedCredentials) {
        currentState.signedInWithSavedCredentials.let {
            if (it == true) {
                onSignedInWithSavedCredentials()
            } else if(it == false) {
                onNotSignedIn()
            }
        }
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingProgressIndicatorComponent()
    }
}


@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun InitializeScreenLightPreview() {
    AppTheme {
        InitializeScreen(
            onSignedInWithSavedCredentials = {},
            onNotSignedIn = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InitializeScreenDarkPreview() {
    AppTheme {
        InitializeScreen(
            onSignedInWithSavedCredentials = {},
            onNotSignedIn = {}
        )
    }
}