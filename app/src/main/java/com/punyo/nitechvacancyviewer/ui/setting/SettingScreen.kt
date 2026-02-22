package com.punyo.nitechvacancyviewer.ui.setting

import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.net.toUri
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.punyo.nitechvacancyviewer.R
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import com.punyo.nitechvacancyviewer.theme.AppTheme
import com.punyo.nitechvacancyviewer.ui.navigation.ScreenDestinations

@Composable
fun SettingScreen(
    modifier: Modifier = Modifier,
    navHostController: NavHostController,
    settingsComponentViewModel: SettingsComponentViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val resources= LocalResources.current
    val currentState by settingsComponentViewModel.uiState.collectAsState()
    val versionName =
        context.packageManager.getPackageInfo(context.packageName, 0)?.versionName ?: ""

    SettingScreenInternal(
        modifier = modifier,
        state = currentState,
        versionName = versionName,
        onOpenOsAppSettings = {
            val intent =
                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", context.packageName, null)
                }
            context.startActivity(intent)
        },
        onShowThemePicker = {
            settingsComponentViewModel.showThemePickerDialog()
        },
        onOpenTos = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = resources.getString(R.string.URL_TOS).toUri()
            context.startActivity(intent)
        },
        onOpenPrivacyPolicy = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = resources.getString(R.string.URL_PRIVACY_POLICY).toUri()
            context.startActivity(intent)
        },
        onOpenStorePage = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = resources.getString(R.string.URL_STORE_PAGE).toUri()
            context.startActivity(intent)
        },
        onOpenSourceCode = {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = resources.getString(R.string.URL_SOURCE_CODE).toUri()
            context.startActivity(intent)
        },
        onOpenLicense = {
            context.startActivity(Intent(context, OssLicensesMenuActivity::class.java))
        },
        onSignOut = {
            settingsComponentViewModel.signOut()
            navHostController.navigate(ScreenDestinations.SignIn.name)
        },
        onThemeConfirm = { theme ->
            settingsComponentViewModel.saveThemeSetting(theme)
            settingsComponentViewModel.hideThemePickerDialog()
        },
        onThemeDismiss = {
            settingsComponentViewModel.hideThemePickerDialog()
        },
    )
}

@Composable
private fun SettingScreenInternal(
    modifier: Modifier = Modifier,
    state: SettingsComponentUiState,
    versionName: String,
    onOpenOsAppSettings: () -> Unit = {},
    onShowThemePicker: () -> Unit = {},
    onOpenTos: () -> Unit = {},
    onOpenPrivacyPolicy: () -> Unit = {},
    onOpenStorePage: () -> Unit = {},
    onOpenSourceCode: () -> Unit = {},
    onOpenLicense: () -> Unit = {},
    onSignOut: () -> Unit = {},
    onThemeConfirm: (ThemeSettings) -> Unit = {},
    onThemeDismiss: () -> Unit = {},
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
    ) {
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.UI_TEXT_GENERAL_SETTINGS),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
        )
        SettingsListItem(
            title = R.string.UI_TEXT_OPEN_OS_APP_SETTINGS,
            icon = Icons.Outlined.Settings,
            onClick = onOpenOsAppSettings,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_THEME,
            icon = R.drawable.outline_dark_mode_24,
            onClick = onShowThemePicker,
        )
        HorizontalDivider()
        ListItem(
            headlineContent = {
                Text(
                    text = stringResource(id = R.string.UI_TEXT_ABOUT_APP),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            },
        )
        SettingsListItem(
            title = R.string.UI_TEXT_TOS,
            icon = R.drawable.outline_launch_24,
            onClick = onOpenTos,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_PRIVACY_POLICY,
            icon = R.drawable.outline_launch_24,
            onClick = onOpenPrivacyPolicy,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_STORE_PAGE,
            icon = R.drawable.outline_launch_24,
            onClick = onOpenStorePage,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_SOURCE_CODE,
            icon = R.drawable.outline_launch_24,
            onClick = onOpenSourceCode,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_LICENSE,
            icon = R.drawable.outline_launch_24,
            onClick = onOpenLicense,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_VERSION,
            subtitle = versionName,
            icon = Icons.Outlined.Info,
        )
        SettingsListItem(
            title = R.string.UI_TEXT_SIGN_OUT,
            icon = Icons.Outlined.Person,
            onClick = onSignOut,
        )
    }
    if (state.showThemePickerDialog) {
        ThemePickerDialogComponent(
            currentTheme = state.currentTheme,
            onConfirm = onThemeConfirm,
            onDismissRequest = onThemeDismiss,
        )
    }
}

@Composable
private fun SettingsListItem(
    @StringRes title: Int,
    subtitle: String = "",
    @DrawableRes icon: Int,
    onClick: () -> Unit = {},
) {
    SettingsListItemImpl(onClick, title, subtitle) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = null,
        )
    }
}

@Composable
private fun SettingsListItem(
    @StringRes title: Int,
    subtitle: String = "",
    icon: ImageVector,
    onClick: () -> Unit = {},
) {
    SettingsListItemImpl(onClick, title, subtitle) {
        Icon(
            imageVector = icon,
            contentDescription = null,
        )
    }
}

@Composable
private fun SettingsListItemImpl(
    onClick: () -> Unit,
    title: Int,
    subtitle: String,
    leadingContent: @Composable () -> Unit,
) {
    ListItem(
        modifier = Modifier.clickable(onClick = onClick),
        headlineContent = { Text(text = stringResource(id = title)) },
        supportingContent = {
            if (subtitle.isNotEmpty()) {
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        },
        leadingContent = leadingContent,
    )
}

@Composable
fun ThemePickerDialogComponent(
    currentTheme: ThemeSettings,
    onConfirm: (ThemeSettings) -> Unit,
    onDismissRequest: () -> Unit,
) {
    var currentSelectedTheme by rememberSaveable { mutableStateOf(currentTheme) }
    Dialog(onDismissRequest) {
        Card(
            modifier = Modifier.defaultMinSize(minWidth = 280.dp),
            shape = MaterialTheme.shapes.extraLarge,
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
            ) {
                Box(
                    Modifier
                        .padding(bottom = 16.dp)
                        .align(Alignment.Start),
                ) {
                    Text(
                        text = stringResource(id = R.string.UI_DIALOG_SELECT_THEME_TITLE),
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
                ThemePickerRadioButtonComponent(
                    title = R.string.UI_TEXT_THEME_LIGHT,
                    currentTheme = currentSelectedTheme,
                    pickerTheme = ThemeSettings.LIGHT,
                ) {
                    currentSelectedTheme = ThemeSettings.LIGHT
                }
                ThemePickerRadioButtonComponent(
                    title = R.string.UI_TEXT_THEME_DARK,
                    currentTheme = currentSelectedTheme,
                    pickerTheme = ThemeSettings.DARK,
                ) {
                    currentSelectedTheme = ThemeSettings.DARK
                }
                ThemePickerRadioButtonComponent(
                    title = R.string.UI_TEXT_THEME_SYSTEM,
                    currentTheme = currentSelectedTheme,
                    pickerTheme = ThemeSettings.SYSTEM,
                ) {
                    currentSelectedTheme = ThemeSettings.SYSTEM
                }
                Box(
                    Modifier
                        .padding(top = 24.dp)
                        .align(Alignment.End),
                ) {
                    Row {
                        TextButton(onDismissRequest) {
                            Text(
                                text = stringResource(id = R.string.UI_TEXT_CANCEL),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                        TextButton({ onConfirm(currentSelectedTheme) }) {
                            Text(
                                text = stringResource(id = R.string.UI_TEXT_OK),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ThemePickerRadioButtonComponent(
    @StringRes title: Int,
    currentTheme: ThemeSettings,
    pickerTheme: ThemeSettings,
    onClick: () -> Unit,
) {
    Row {
        RadioButton(
            onClick = onClick,
            selected = currentTheme == pickerTheme,
        )
        Text(
            modifier = Modifier.align(Alignment.CenterVertically),
            text = stringResource(id = title),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
private fun SettingScreenPreview() {
    AppTheme {
        SettingScreenInternal(
            state =
            SettingsComponentUiState(
                currentTheme = ThemeSettings.LIGHT,
                showThemePickerDialog = false,
            ),
            versionName = "1.0.0",
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ThemePickerDialogComponentPreview() {
    AppTheme {
        ThemePickerDialogComponent(
            onDismissRequest = {},
            currentTheme = ThemeSettings.LIGHT,
            onConfirm = {},
        )
    }
}
