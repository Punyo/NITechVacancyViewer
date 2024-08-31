package com.punyo.nitechroomvacancyviewer.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.punyo.nitechroomvacancyviewer.R
import com.punyo.nitechroomvacancyviewer.ui.model.MainScreenViewModel
import com.punyo.nitechroomvacancyviewer.ui.theme.AppTheme

@Composable
fun MainScreen(modifier: Modifier = Modifier, mainviewmodel: MainScreenViewModel = viewModel()) {
    val currentState by mainviewmodel.uiState.collectAsStateWithLifecycle()
    val navbarLabels = listOf(
        stringResource(id = R.string.UI_NAVIGATIONBARITEM_TEXT_HOME),
        stringResource(id = R.string.UI_NAVIGATIONBARITEM_TEXT_VACANCY),
        stringResource(id = R.string.UI_NAVIGATIONBARITEM_TEXT_SETTINGS)
    )
    val navbarIcons = listOf(
        Icons.Default.Home,
        Icons.AutoMirrored.Filled.List,
        Icons.Default.Settings
    )
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = { MainScreenAppBar(currentNavTitle = navbarLabels[currentState.currentNavIndex]) },
        bottomBar = {
            NavigationBar {
                navbarLabels.forEachIndexed { index, label ->
                    NavigationBarItem(
                        icon = { Icon(navbarIcons[index], contentDescription = null) },
                        label = { Text(label) },
                        selected = index == currentState.currentNavIndex,
                        onClick = {
                            mainviewmodel.onNavItemClick(index)
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        when (currentState.currentNavIndex) {
            0 -> HomeComponent(modifier.padding(innerPadding))
            1 -> VacancyComponent(modifier.padding(innerPadding), arrayOf())
            2 -> SettingsComponent(modifier.padding(innerPadding))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreenAppBar(modifier: Modifier = Modifier, currentNavTitle: String) {
    TopAppBar(
        modifier = modifier,
        title = { Text(text = currentNavTitle) },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_NO)
@Composable
fun LightModePreview() {
    AppTheme {
        MainScreen()
    }
}

@Preview(showBackground = true, uiMode = android.content.res.Configuration.UI_MODE_NIGHT_YES)
@Composable
fun DarkModePreview() {
    AppTheme {
        MainScreen()
    }
}