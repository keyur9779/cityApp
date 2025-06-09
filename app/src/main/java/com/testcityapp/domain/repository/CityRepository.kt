package com.testcityapp.domain.repository

import com.testcityapp.domain.model.CityEmission
import kotlinx.coroutines.flow.Flow

interface CityRepository {
    fun getCityEmissions(): Flow<List<CityEmission>>
    fun startProducing()
    fun stopProducing()
}
