package com.testcityapp.domain.usecase

import com.testcityapp.data.repository.CityRepository
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test

class StartEmissionProductionUseCaseTest {

    private lateinit var repository: CityRepository
    private lateinit var useCase: StartEmissionProductionUseCase

    @Before
    fun setUp() {
        repository = mockk(relaxed = true)
        useCase = StartEmissionProductionUseCase(repository)
    }

    @Test
    fun `invoke should call startProducing on repository`() {
        // Given
        justRun { repository.startProducing() }

        // When
        useCase()

        // Then
        verify(exactly = 1) { repository.startProducing() }
    }
}
