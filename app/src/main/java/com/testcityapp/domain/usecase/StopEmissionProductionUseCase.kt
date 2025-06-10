package com.testcityapp.domain.usecase

import com.testcityapp.data.repository.CityRepository

class StopEmissionProductionUseCase(private val repository: CityRepository) {
    operator fun invoke() = repository.stopProducing()
}
