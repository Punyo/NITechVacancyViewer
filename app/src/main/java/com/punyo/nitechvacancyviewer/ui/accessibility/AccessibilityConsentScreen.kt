package com.punyo.nitechvacancyviewer.ui.accessibility

import android.app.Activity
import android.content.Intent
import android.provider.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.theme.AppTheme

@Composable
fun AccessibilityConsentScreen(
    accessibilityConsentScreenViewModel: AccessibilityConsentScreenViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val currentState by accessibilityConsentScreenViewModel.uiState.collectAsStateWithLifecycle()
    LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
        accessibilityConsentScreenViewModel.resetUserConsentState()
    }
    LaunchedEffect(key1 = currentState.userConsentState)
    {
        currentState.userConsentState?.let {
            if (!accessibilityConsentScreenViewModel.isAccessibilityServiceEnabled() && it) {
                context.startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
            } else {
                (context as? Activity)?.finish()
            }
        }
    }
    AccessibilityConsentScreenInternal(
        onConfirm = {
            accessibilityConsentScreenViewModel.setUserConsentState(true)
        },
        onDismiss = {
            accessibilityConsentScreenViewModel.setUserConsentState(false)
        },
    )
}

@Composable
private fun AccessibilityConsentScreenInternal(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        title = {
            Text(text = stringResource(id = R.string.UI_DIALOG_ACCESSIBILITY_CONSENT_TITLE))
        },
        text = {
            Text(text = stringResource(id = R.string.UI_DIALOG_ACCESSIBILITY_CONSENT_TEXT))
        },
        onDismissRequest = { onDismiss() },
        confirmButton = {
            TextButton(onClick = { onConfirm() }) {
                Text(stringResource(id = R.string.UI_DIALOG_ACCESSIBILITY_CONSENT_BUTTON_CONFIRM))
            }
        },
        dismissButton = {
            TextButton(onClick = { onDismiss() }) {
                Text(stringResource(id = R.string.UI_TEXT_CANCEL))
            }
        },
    )
}

@Preview(showBackground = true)
@Composable
private fun AccessibilityConsentScreenPreview() {
    AppTheme {
        AccessibilityConsentScreenInternal(
            onConfirm = {},
            onDismiss = {},
        )
    }
}

