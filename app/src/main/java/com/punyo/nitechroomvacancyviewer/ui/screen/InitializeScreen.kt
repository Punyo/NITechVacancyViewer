package com.punyo.nitechroomvacancyviewer.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.microsoft.identity.client.exception.MsalException
import com.punyo.nitechroomvacancyviewer.data.MSGraphRepository
import com.punyo.nitechroomvacancyviewer.ui.model.InitializeScreenViewModel
import com.punyo.nitechroomvacancyviewer.ui.theme.AppTheme

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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                modifier = Modifier
                    .width(48.dp)
                    .height(48.dp),
                color = MaterialTheme.colorScheme.primary,
                trackColor = MaterialTheme.colorScheme.secondaryContainer
            )
        }
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

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun InitializeScreenLightPreview() {
    AppTheme {
        InitializeScreen(
            onAlreadySignedIn = {},
            onNotSignedIn = {},
            onInitializeFailed = {}
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InitializeScreenDarkPreview() {
    AppTheme {
        InitializeScreen(
            onAlreadySignedIn = {},
            onNotSignedIn = {},
            onInitializeFailed = {}
        )
    }
}