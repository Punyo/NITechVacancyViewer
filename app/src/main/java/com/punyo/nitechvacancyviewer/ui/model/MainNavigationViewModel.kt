package com.punyo.nitechvacancyviewer.ui.model

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.punyo.nitechvacancyviewer.data.setting.SettingRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

class MainNavigationViewModel(
    application: Application,
    private val settingRepository: SettingRepository
) : AndroidViewModel(application) {

    val currentTheme =
        settingRepository.themeSettings.stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )

    class Factory(
        private val context: Application,
        private val settingRepository: SettingRepository
    ) :
        ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return MainNavigationViewModel(context, settingRepository) as T
        }
    }
}