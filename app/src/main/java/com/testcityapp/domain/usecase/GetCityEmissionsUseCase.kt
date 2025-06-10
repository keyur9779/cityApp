package com.testcityapp.domain.usecase

import com.testcityapp.data.repository.CityRepository

class GetCityEmissionsUseCase(private val repository: CityRepository) {
    operator fun invoke() = repository.getCityEmissions()
}