package com.testcityapp.domain.usecase

import com.testcityapp.domain.repository.CityRepository

class GetCityEmissionsUseCase(private val repository: CityRepository) {
    operator fun invoke() = repository.getCityEmissions()
}