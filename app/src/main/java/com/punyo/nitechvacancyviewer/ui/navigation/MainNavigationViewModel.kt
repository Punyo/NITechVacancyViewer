package com.punyo.nitechvacancyviewer.ui.navigation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.setting.SettingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainNavigationViewModel
    @Inject
    constructor(
        settingRepository: SettingRepository,
    ) : ViewModel() {
        val currentTheme =
            settingRepository.themeSettings.stateIn(
                viewModelScope,
                SharingStarted.Eagerly,
                false,
            )
    }
