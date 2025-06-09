package com.testcityapp.di

import com.testcityapp.data.repository.CityRepositoryImpl
import com.testcityapp.domain.repository.CityRepository
import com.testcityapp.domain.usecase.GetCityEmissionsUseCase
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {
    
    @Provides
    @Singleton
    fun provideGetCityEmissionsUseCase(repository: CityRepositoryImpl): GetCityEmissionsUseCase {
        return GetCityEmissionsUseCase(repository)
    }


}
