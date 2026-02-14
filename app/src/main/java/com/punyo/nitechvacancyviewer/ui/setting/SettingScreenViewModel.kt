package com.punyo.nitechvacancyviewer.ui.setting

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechvacancyviewer.data.room.RoomRepositoryImpl
import com.punyo.nitechvacancyviewer.data.setting.SettingRepository
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsComponentViewModel
@Inject
constructor(
    private val applicationContext: Context,
    private val authRepository: AuthRepository,
    private val roomRepository: RoomRepositoryImpl,
    private val settingRepository: SettingRepository,
) : ViewModel() {
    private val state = MutableStateFlow(SettingsComponentUiState())
    val uiState = state.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            authRepository.signOutAndClearSavedCredentials(applicationContext)
            roomRepository.clearDB(applicationContext)
        }
    }

    fun saveThemeSetting(themeSettings: ThemeSettings) {
        viewModelScope.launch {
            settingRepository.saveThemeSetting(themeSettings)
        }
    }

    fun showThemePickerDialog() {
        viewModelScope.launch {
            state.value =
                state.value.copy(
                    showThemePickerDialog = true,
                    currentTheme = settingRepository.themeSettings.first(),
                )
        }
    }

    fun hideThemePickerDialog() {
        viewModelScope.launch {
            state.value = state.value.copy(showThemePickerDialog = false)
        }
    }
}

data class SettingsComponentUiState(
    val currentTheme: ThemeSettings = ThemeSettings.LIGHT,
    val showThemePickerDialog: Boolean = false,
)
