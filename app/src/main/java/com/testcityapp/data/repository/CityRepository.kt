package com.testcityapp.data.repository

import com.testcityapp.domain.model.CityEmission
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getCityEmissions(): Flow<List<CityEmission>>
    suspend fun insertEmission(emission: CityEmission)
    fun startProducing()
    fun stopProducing()
}