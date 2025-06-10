package com.testcityapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CityDao {
    @Query("SELECT * FROM emissions ORDER BY city ASC")
    fun getAllEmissions(): Flow<List<CityEmissionEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEmission(emission: CityEmissionEntity)
    
    @Query("SELECT * FROM emissions WHERE city = :cityName LIMIT 1")
    suspend fun getEmissionByCity(cityName: String): CityEmissionEntity?
    
    @Update
    suspend fun updateEmission(emission: CityEmissionEntity)
}