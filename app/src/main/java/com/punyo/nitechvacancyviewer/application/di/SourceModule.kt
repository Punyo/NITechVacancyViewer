package com.punyo.nitechvacancyviewer.application.di

import android.content.Context
import com.punyo.nitechvacancyviewer.data.auth.source.AuthNetworkDataSource
import com.punyo.nitechvacancyviewer.data.auth.source.UserCredentialsLocalDataSource
import com.punyo.nitechvacancyviewer.data.building.source.BuildingLocalDataSource
import com.punyo.nitechvacancyviewer.data.room.source.RoomLocalDataSource
import com.punyo.nitechvacancyviewer.data.setting.source.SettingLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceModule {
    @Provides
    @Singleton
    fun provideBuildingLocalDataSource(): BuildingLocalDataSource = BuildingLocalDataSource()

    @Provides
    @Singleton
    fun provideAuthNetworkDataSource(): AuthNetworkDataSource = AuthNetworkDataSource()

    @Provides
    @Singleton
    fun provideUserCredentialsLocalDataSource(): UserCredentialsLocalDataSource = UserCredentialsLocalDataSource()

    @Provides
    @Singleton
    fun provideRoomLocalDataSource(): RoomLocalDataSource = RoomLocalDataSource()

    @Provides
    @Singleton
    fun provideSettingsLocalDataSource(): SettingLocalDataSource = SettingLocalDataSource()

    @Provides
    @Singleton
    fun provideApplicationContext(
        @ApplicationContext applicationContext: Context,
    ): Context = applicationContext
}
