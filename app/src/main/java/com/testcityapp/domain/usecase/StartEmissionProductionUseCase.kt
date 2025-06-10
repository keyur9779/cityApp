package com.testcityapp.domain.usecase

import com.testcityapp.data.repository.CityRepository

class StartEmissionProductionUseCase(private val repository: CityRepository) {
    operator fun invoke() = repository.startProducing()
}
