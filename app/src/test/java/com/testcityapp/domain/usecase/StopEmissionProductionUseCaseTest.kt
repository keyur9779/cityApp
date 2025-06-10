package com.testcityapp.domain.usecase

import com.testcityapp.data.repository.CityRepository
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class StopEmissionProductionUseCaseTest {

    private lateinit var repository: CityRepository
    private lateinit var useCase: StopEmissionProductionUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = StopEmissionProductionUseCase(repository)
    }

    @Test
    fun `invoke should call stopProducing on repository`() {
        // Given
        justRun { repository.stopProducing() }

        // When
        useCase()

        // Then
        verify(exactly = 1) { repository.stopProducing() }
    }
}
