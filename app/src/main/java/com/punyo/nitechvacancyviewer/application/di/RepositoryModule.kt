package com.punyo.nitechvacancyviewer.application.di

import com.punyo.nitechvacancyviewer.data.building.BuildingRepository
import com.punyo.nitechvacancyviewer.data.building.BuildingRepositoryImpl
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
}
