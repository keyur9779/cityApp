package com.testcityapp.di

import android.app.Application
import androidx.room.Room
import com.testcityapp.data.local.CityDao
import com.testcityapp.data.local.CityDatabase
import com.testcityapp.data.producer.CityEmissionProducer
import com.testcityapp.data.repository.CityRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
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
    fun provideCityEmissionProducer(): CityEmissionProducer {
        return CityEmissionProducer()
    }
    
    /**
     * Provides a CoroutineDispatcher for dependency injection.
     * Using IO dispatcher for actual app usage and potentially a test dispatcher for tests.
     */
    @Provides
    @Singleton
    fun provideCoroutineDispatcher(): CoroutineDispatcher {
        return Dispatchers.IO
    }
}
