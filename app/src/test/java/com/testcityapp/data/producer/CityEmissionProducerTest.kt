package com.testcityapp.data.producer

import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ProcessLifecycleOwner
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(manifest = Config.NONE)
class CityEmissionProducerTest {

    private lateinit var producer: CityEmissionProducer

    @Before
    fun setUp() {
        // Create a proper mocked lifecycle owner
        val lifecycleOwner = mockk<ProcessLifecycleOwner>(relaxed = true)
        val lifecycle = LifecycleRegistry(lifecycleOwner)
        lifecycle.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        
        // Mock ProcessLifecycleOwner static methods
        //every { ProcessLifecycleOwner.get() } returns lifecycleOwner
        every { lifecycleOwner.lifecycle } returns lifecycle
        
        // Create the producer
        producer = CityEmissionProducer()
    }
    
    @Test
    fun `test producer emits random city-color combinations`() = runBlocking {
        // Collect 3 emissions for testing
        val emissions = producer.produceEmissions()
            .take(3)
            .toList()
        
        // Verify we received 3 emissions
        assert(emissions.size == 3)
        
        // Verify each emission has valid city and color
        emissions.forEach { emission ->
            assert(emission.city.isNotBlank())
            assert(emission.color.isNotBlank())
            assert(emission.latitude != 0.0) // Should have a real coordinate
            assert(emission.longitude != 0.0) // Should have a real coordinate
        }
    }
}
