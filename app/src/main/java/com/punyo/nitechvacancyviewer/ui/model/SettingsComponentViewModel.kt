package com.punyo.nitechvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.data.setting.SettingRepository
import com.punyo.nitechvacancyviewer.data.setting.model.ThemeSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class SettingsComponentViewModel(
    application: Application,
    private val roomRepository: RoomRepository,
    private val settingRepository: SettingRepository
) : AndroidViewModel(application) {

    private val state = MutableStateFlow(SettingsComponentUiState())
    val uiState = state.asStateFlow()

    fun signOut() {
        viewModelScope.launch {
            AuthRepository.signOutAndClearSavedCredentials(getApplication())
            roomRepository.clearDB(getApplication())
        }
    }

    fun saveThemeSetting(themeSettings: ThemeSettings) {
        viewModelScope.launch {
            settingRepository.saveThemeSetting(themeSettings)
        }
    }

    fun showThemePickerDialog() {
        viewModelScope.launch {
            state.value = state.value.copy(
                showThemePickerDialog = true,
                currentTheme = settingRepository.themeSettings.first()
            )
        }
    }

    fun hideThemePickerDialog() {
        viewModelScope.launch {
            state.value = state.value.copy(showThemePickerDialog = false)
        }
    }

    class Factory(
        private val context: Application,
        private val roomRepository: RoomRepository,
        private val settingRepository: SettingRepository
    ) :
        ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(SettingsComponentViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return SettingsComponentViewModel(context, roomRepository, settingRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}

data class SettingsComponentUiState(
    val currentTheme: ThemeSettings = ThemeSettings.LIGHT,
    val showThemePickerDialog: Boolean = false
)