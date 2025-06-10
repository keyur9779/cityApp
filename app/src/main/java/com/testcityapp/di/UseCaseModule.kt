package com.testcityapp.di

import com.testcityapp.data.repository.CityRepository
import com.testcityapp.domain.usecase.GetCityEmissionsUseCase
import com.testcityapp.domain.usecase.StartEmissionProductionUseCase
import com.testcityapp.domain.usecase.StopEmissionProductionUseCase
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
    fun provideGetCityEmissionsUseCase(repository: CityRepository): GetCityEmissionsUseCase {
        return GetCityEmissionsUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideStartEmissionProductionUseCase(repository: CityRepository): StartEmissionProductionUseCase {
        return StartEmissionProductionUseCase(repository)
    }
    
    @Provides
    @Singleton
    fun provideStopEmissionProductionUseCase(repository: CityRepository): StopEmissionProductionUseCase {
        return StopEmissionProductionUseCase(repository)
    }
}
