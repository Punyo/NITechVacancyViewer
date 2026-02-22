package com.punyo.nitechvacancyviewer.application.di

import com.punyo.nitechvacancyviewer.data.auth.AuthRepository
import com.punyo.nitechvacancyviewer.data.auth.AuthRepositoryImpl
import com.punyo.nitechvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechvacancyviewer.data.building.BuildingRepositoryImpl
import com.punyo.nitechvacancyviewer.data.room.RoomRepository
import com.punyo.nitechvacancyviewer.data.room.RoomRepositoryImpl
import com.punyo.nitechvacancyviewer.data.setting.SettingRepository
import com.punyo.nitechvacancyviewer.data.setting.SettingRepositoryImpl
import com.punyo.nitechvacancyviewer.data.widget.source.AccessibilityChecker
import com.punyo.nitechvacancyviewer.data.widget.source.AndroidAccessibilityChecker
import com.punyo.nitechvacancyviewer.data.widget.WidgetRepository
import com.punyo.nitechvacancyviewer.data.widget.WidgetRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBuildingRepository(buildingRepositoryImpl: BuildingRepositoryImpl): BuildingRepository

    @Binds
    @Singleton
    abstract fun bindAuthRepository(authRepositoryImpl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindRoomRepository(roomRepositoryImpl: RoomRepositoryImpl): RoomRepository

    @Binds
    @Singleton
    abstract fun bindSettingRepository(settingRepositoryImpl: SettingRepositoryImpl): SettingRepository

    @Binds
    @Singleton
    abstract fun bindWidgetRepository(widgetRepositoryImpl: WidgetRepositoryImpl): WidgetRepository

    @Binds
    @Singleton
    abstract fun bindAccessibilityChecker(androidAccessibilityChecker: AndroidAccessibilityChecker): AccessibilityChecker
}
