package com.punyo.nitechvacancyviewer.ui.initialize

import android.app.Activity
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarData
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.component.CampusSquareWebViewComponent
import com.punyo.nitechvacancyviewer.ui.component.LoadingProgressIndicatorComponent

@Composable
fun InitializeScreen(
    onLoadedRoomsData: () -> Unit,
    onFailedSignInWithSavedCredentials: () -> Unit,
    initializeScreenViewModel: InitializeScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val currentState by initializeScreenViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(key1 = Unit) {
        initializeScreenViewModel.initialize()
    }
    LaunchedEffect(key1 = currentState.loadedRoomsDataFromDB) {
        currentState.loadedRoomsDataFromDB?.let {
            if (it) {
                onLoadedRoomsData()
            } else {
                if (currentState.signedInWithSavedCredentialsOrAlreadySignedIn != true) {
                    initializeScreenViewModel.changeAskForSignInDialogVisibility(true)
                }
            }
        }
    }
    LaunchedEffect(key1 = currentState.signedInWithSavedCredentialsOrAlreadySignedIn) {
        currentState.signedInWithSavedCredentialsOrAlreadySignedIn?.let {
            if (it) {
                initializeScreenViewModel.activateCampusSquareWebView()
            } else {
                onFailedSignInWithSavedCredentials()
            }
        }
    }
    LaunchedEffect(key1 = currentState.loadedRoomsDataFromCampusSquare) {
        currentState.loadedRoomsDataFromCampusSquare?.let {
            if (it) {
                onLoadedRoomsData()
            } else {
                initializeScreenViewModel.setErrorMessage(
                    context.getString(R.string.ERROR_PREFIX_FETCH_FAILED) +
                        context.getString(
                            R.string.ERROR_VACANCY_TABLE_LAYOUT_CHANGED,
                        ),
                    context.getString(R.string.UI_SNACKBAR_ACTIONLABEL_QUITAPP),
                ) {
                    (context as Activity).finish()
                }
            }
        }
    }
    LaunchedEffect(key1 = currentState.errorMessage) {
        currentState.errorMessage?.let {
            val result =
                snackbarHostState.showSnackbar(
                    message = it.message,
                    actionLabel = it.actionLabel,
                    duration = SnackbarDuration.Indefinite,
                )
            if (result == SnackbarResult.ActionPerformed) {
                it.onClickAction()
            }
            initializeScreenViewModel.currentErrorShowed()
        }
    }
    if (currentState.showAskForSignInDialog) {
        AskForSignInDialogComponent(
            dialogTitle = R.string.UI_DIALOG_SIGN_IN_TITLE,
            dialogText = R.string.UI_DIALOG_SIGN_IN_TEXT,
            confirmButtonText = R.string.UI_DIALOG_SIGN_IN_BUTTON_CONFIRM,
            dismissButtonText = R.string.UI_DIALOG_SIGN_IN_BUTTON_DISMISS,
            onDismissRequest = {
                (context as Activity).finish()
            },
            onConfirmation = {
                initializeScreenViewModel.changeAskForSignInDialogVisibility(false)
                initializeScreenViewModel.tryToSignInWithSavedCredentials()
            },
        )
    }
    if (currentState.isActivatedCampusSquareWebView) {
        CampusSquareWebViewComponent(
            onGetReservationTableHTML = { html ->
                initializeScreenViewModel.tryToLoadRoomsDataFromHTML(html)
            },
            onReceivedError = { webView, _ ->
                initializeScreenViewModel.setErrorMessage(
                    context.getString(R.string.ERROR_PREFIX_FETCH_FAILED) +
                        context.getString(
                            R.string.ERROR_NOT_CONNECTED_TO_NITECH_NETWORK,
                        ),
                    context.getString(R.string.UI_SNACKBAR_ACTIONLABEL_RETRY),
                ) {
                    webView?.reload()
                }
            },
            onReceivedHttpError = { _, error ->
                val statusCode = error?.statusCode
                statusCode?.let {
                    if (it == 503) {
                        initializeScreenViewModel.setErrorMessage(
                            context.getString(R.string.ERROR_PREFIX_FETCH_FAILED) +
                                context.getString(
                                    R.string.ERROR_HTTP_503,
                                ),
                            context.getString(R.string.UI_SNACKBAR_ACTIONLABEL_QUITAPP),
                        ) {
                            (context as Activity).finish()
                        }
                    } else {
                        initializeScreenViewModel.setErrorMessage(
                            context.getString(R.string.ERROR_PREFIX_FETCH_FAILED) +
                                context
                                    .getString(
                                        R.string.ERROR_HTTP_OTHERS,
                                    ).format(statusCode),
                            context.getString(R.string.UI_SNACKBAR_ACTIONLABEL_QUITAPP),
                        ) {
                            (context as Activity).finish()
                        }
                    }
                }
            },
            sso4cookie = initializeScreenViewModel.getCurrentToken()!!,
        )
    }
    Scaffold(snackbarHost = {
        SnackbarHost(
            hostState = snackbarHostState,
            snackbar = { snackbarData ->
                LongerActionSnackbarComponent(snackbarData)
            },
        )
    }) { contentPadding ->
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(contentPadding),
            contentAlignment = Alignment.Center,
        ) {
            LoadingProgressIndicatorComponent()
        }
    }
}

@Composable
private fun LongerActionSnackbarComponent(snackbarData: SnackbarData) {
    Card(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
    ) {
        Row {
            Column(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start,
            ) {
                Text(text = snackbarData.visuals.message)
                snackbarData.visuals.actionLabel?.let {
                    TextButton(
                        modifier = Modifier.align(Alignment.End),
                        onClick = { snackbarData.performAction() },
                        content = { Text(snackbarData.visuals.actionLabel!!) },
                    )
                }
            }
        }
    }
}

@Composable
fun AskForSignInDialogComponent(
    @StringRes dialogTitle: Int,
    @StringRes dialogText: Int,
    @StringRes confirmButtonText: Int,
    @StringRes dismissButtonText: Int,
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = dialogTitle))
        },
        text = {
            Text(text = stringResource(id = dialogText))
        },
        onDismissRequest = {
            onDismissRequest()
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirmation()
                },
            ) {
                Text(stringResource(id = confirmButtonText))
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onDismissRequest()
                },
            ) {
                Text(stringResource(id = dismissButtonText))
            }
        },
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun InitializeScreenLightPreview() {
    AppTheme {
        InitializeScreen(
            onLoadedRoomsData = {},
            onFailedSignInWithSavedCredentials = {},
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun InitializeScreenDarkPreview() {
    AppTheme {
        InitializeScreen(
            onLoadedRoomsData = {},
            onFailedSignInWithSavedCredentials = {},
        )
    }
}
