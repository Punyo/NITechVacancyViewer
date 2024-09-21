package com.punyo.nitechvacancyviewer.ui.screen

import android.app.Application
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechvacancyviewer.ui.model.SignInScreenViewModel
import com.punyo.nitechvacancyviewer.ui.theme.AppTheme

@Composable
fun SignInScreen(
    onSignInSuccess: () -> Unit = {},
    signInScreenViewModel: SignInScreenViewModel = viewModel(
        factory = SignInScreenViewModel.Factory(
            LocalContext.current.applicationContext as Application
        )
    )
) {
    val currentState by signInScreenViewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.primaryContainer,
        snackbarHost = { SnackbarHost(snackbarHostState) }
    )
    { innerPadding ->
        LaunchedEffect(key1 = currentState.signInResult) {
            currentState.signInResult?.let {
                var failReason = context.getString(R.string.ERROR_PREFIX_SIGN_IN_FAILED)
                when (it) {
                    AuthRepository.AuthResultStatus.SUCCESS -> {
                        onSignInSuccess()
                    }

                    AuthRepository.AuthResultStatus.NETWORK_ERROR -> {
                        failReason += (context.getString(R.string.ERROR_NOT_CONNECTED_TO_NITECH_NETWORK))
                    }

                    AuthRepository.AuthResultStatus.INVALID_CREDENTIALS -> {
                        failReason += (context.getString(R.string.ERROR_SIGN_IN_DENIED_OR_INVALID_CREDENTIALS))
                    }

                    AuthRepository.AuthResultStatus.UNKNOWN_ERROR -> {
                        failReason += (context.getString(R.string.ERROR_UNKNOWN_ERROR))
                    }

                    AuthRepository.AuthResultStatus.CREDENTIALS_NOT_FOUND -> {
                    }
                }
                if (it != AuthRepository.AuthResultStatus.SUCCESS) {
                    snackbarHostState.showSnackbar(failReason)
                }
                signInScreenViewModel.setSignInButtonEnabled(true)
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
                contentDescription = stringResource(id = R.string.APP_NAME)
            )
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(id = R.string.APP_NAME),
                textAlign = TextAlign.Center,
                fontSize = MaterialTheme.typography.titleLarge.fontSize,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                value = currentState.userName,
                label = { Text(text = stringResource(id = R.string.UI_TEXTFIELD_TEXT_USERNAME)) },
                singleLine = true,
                isError = currentState.isErrorUserName,
                supportingText = {
                    if (currentState.isErrorUserName) {
                        Text(stringResource(id = R.string.UI_TEXTFIELD_SUPPORTINGTEXT_EMPTY))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Uri,
                    imeAction = ImeAction.Next,
                ),
                placeholder = { Text(text = stringResource(id = R.string.UI_TEXTFIELD_TEXT_USERNAME_HINT)) },
                onValueChange = { signInScreenViewModel.setUserName(it) })
            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp),
                value = currentState.password,
                label = { Text(stringResource(id = R.string.UI_TEXTFIELD_TEXT_PASSWORD)) },
                singleLine = true,
                visualTransformation = PasswordVisualTransformation(),
                isError = currentState.isErrorPassword,
                supportingText = {
                    if (currentState.isErrorPassword) {
                        Text(stringResource(id = R.string.UI_TEXTFIELD_SUPPORTINGTEXT_EMPTY))
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                placeholder = { Text(stringResource(id = R.string.UI_TEXTFIELD_TEXT_PASSWORD_HINT)) },
                onValueChange = { signInScreenViewModel.setPassword(it) })
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 8.dp)
                    .height(40.dp),
                enabled = currentState.isSignInButtonEnabled,
                onClick = {
                    signInScreenViewModel.onSignInButtonClicked(onSignInSuccess)
                }) {
                Text(
                    text = if (currentState.isSignInButtonEnabled) {
                        stringResource(id = R.string.UI_BUTTON_TEXT_LOGIN)
                    } else {
                        stringResource(id = R.string.UI_BUTTON_TEXT_LOGIN_ATTEMPTING)
                    }
                )
            }
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
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
        SignInScreen(signInScreenViewModel = SignInScreenViewModel(Application()))
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun LoginScreenDarkPreview() {
    AppTheme {
        SignInScreen(signInScreenViewModel = SignInScreenViewModel(Application()))
    }
}