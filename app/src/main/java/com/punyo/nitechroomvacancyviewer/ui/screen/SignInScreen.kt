package com.punyo.nitechroomvacancyviewer.ui.screen

import android.app.Activity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.data.msgraph.MSGraphRepository
import com.punyo.nitechroomvacancyviewer.ui.model.SignInScreenViewModel
import com.punyo.nitechroomvacancyviewer.ui.theme.AppTheme

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit = {},
    isInitializeSuccess: Boolean = true,
    signInScreenViewModel: SignInScreenViewModel = SignInScreenViewModel(
        MSGraphRepository()
    )
) {
    val context = LocalContext.current
    val activity = context as Activity
    val currentState by signInScreenViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    signInScreenViewModel.setInitSuccess(isInitializeSuccess)
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    )
    { innerPadding ->
        LaunchedEffect(key1 = currentState.signInResultStatus, key2 = currentState.isInitSuccess) {
            currentState.signInResultStatus?.let {
                var failReason: String = context.getString(R.string.ERROR_PREFIX_SIGN_IN_FAILED)
                when (it) {
                    MSGraphRepository.MSALOperationResultStatus.SUCCESS -> {
                        onSignInSuccess()
                    }

                    MSGraphRepository.MSALOperationResultStatus.NEED_RE_SIGN_IN -> failReason += context.getString(
                        R.string.ERROR_NEED_RE_SIGN_IN
                    )

                    MSGraphRepository.MSALOperationResultStatus.CANCELLED -> failReason += context.getString(
                        R.string.ERROR_CANCELLED
                    )

                    MSGraphRepository.MSALOperationResultStatus.NETWORK_ERROR -> failReason += context.getString(
                        R.string.ERROR_NETWORK_ERROR
                    )

                    MSGraphRepository.MSALOperationResultStatus.INTERNAL_ERROR -> failReason += context.getString(
                        R.string.ERROR_INTERNAL_ERROR
                    )
                }
                if (currentState.signInResultStatus != MSGraphRepository.MSALOperationResultStatus.SUCCESS) {
                    snackbarHostState.showSnackbar(
                        message = failReason,
                        duration = SnackbarDuration.Short
                    )
                }
            }
            if (currentState.isInitSuccess == false) {
                snackbarHostState.showSnackbar(
                    message = "このアプリの内部に興味がありますか？\n私と一緒に開発しましょう！",
                    duration = SnackbarDuration.Short
                )
            }
        }
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                modifier = Modifier
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .size(200.dp),
                painter = painterResource(id = R.drawable.ic_launcher_foreground),
                contentDescription = stringResource(id = R.string.app_name)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.app_name),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .height(40.dp),
                onClick = {
                    if (isInitializeSuccess) {
                        signInScreenViewModel.onSignInButtonClicked(
                            activity = activity, onSignInSuccess = onSignInSuccess
                        )
                    }
                }) {
                Text(stringResource(id = R.string.UI_TEXT_LOGIN))
            }
        }
        Box(modifier = Modifier.fillMaxSize()) {
            Text(
                text = "注意事項をここに挿入",
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
            )
        }
    }

}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun LoginScreenLightPreview() {
    AppTheme {
        SignInScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    AppTheme {
        SignInScreen()
    }
}