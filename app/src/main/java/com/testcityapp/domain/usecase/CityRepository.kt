package com.testcityapp.domain.usecase

import com.testcityapp.domain.model.CityEmission
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getCityEmissions(): Flow<List<CityEmission>>
    fun startProducing()
    fun stopProducing()
}