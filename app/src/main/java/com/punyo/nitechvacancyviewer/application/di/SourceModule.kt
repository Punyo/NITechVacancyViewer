package com.punyo.nitechvacancyviewer.application.di

import com.punyo.nitechvacancyviewer.data.building.source.BuildingLocalDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object SourceModule {
    @Provides
    @Singleton
    fun provideBuildingLocalDataSource(): BuildingLocalDataSource = BuildingLocalDataSource()
}
