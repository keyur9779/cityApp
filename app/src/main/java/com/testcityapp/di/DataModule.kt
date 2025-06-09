package com.testcityapp.di

import android.app.Application
import androidx.room.Room
import com.testcityapp.data.local.CityDao
import com.testcityapp.data.local.CityDatabase
import com.testcityapp.data.producer.CityEmissionProducer
import com.testcityapp.data.repository.CityRepositoryImpl
import com.testcityapp.domain.repository.CityRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    
    @Provides
    @Singleton
    fun provideCityDatabase(application: Application): CityDatabase {
        return Room.databaseBuilder(
            application,
            CityDatabase::class.java,
            "city_database"
        ).build()
    }
    
    @Provides
    @Singleton
    fun provideCityDao(database: CityDatabase): CityDao {
        return database.cityDao()
    }
    
    @Provides
    @Singleton
    fun provideCityEmissionProducer(application: Application): CityEmissionProducer {
        return CityEmissionProducer(application)
    }
}
