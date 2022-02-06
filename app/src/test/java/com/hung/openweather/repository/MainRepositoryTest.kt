package com.hung.openweather.repository

import com.google.gson.Gson
import com.hung.openweather.data.disk.DiskDataSource
import com.hung.openweather.data.memory.MemoryDataSource
import com.hung.openweather.data.network.NetworkDataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.TestUtils
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito

@RunWith(JUnit4::class)
class MainRepositoryTest {

    private val memoryDataSource = PowerMockito.mock(MemoryDataSource::class.java)
    private val diskDataSource = PowerMockito.mock(DiskDataSource::class.java)
    private val networkDataSource = PowerMockito.mock(NetworkDataSource::class.java)
    private val mainRepository = PowerMockito.spy(MainRepository(memoryDataSource, diskDataSource, networkDataSource))

    /**
     * Given nothing
     * When calling getDailyForecast in MainRepository
     * Then nothing is called
     */
    @Test
    fun testNull() {
        Mockito.verify(mainRepository, Mockito.never()).getDailyForecast("saigon")
    }

    /**
     * Given MemoryDataSource, DiskDataSource, NetworkDataSource
     * When calling getDailyForecast in MainRepository
     * Then getDailyForecast in MemoryDataSource, DiskDataSource, NetworkDataSource must be called
     */
    @Test
    fun testGetDailyForecast() {
        val failure = TestUtils.getJsonFromResource(this, "weather_response_success.json")
        val response = Gson().fromJson(failure, WeatherResponse::class.java)

        Mockito.`when`(memoryDataSource.getDailyForecast("saigon"))
            .thenReturn(Observable.just(response))
        Mockito.verifyNoMoreInteractions(memoryDataSource)

        Mockito.`when`(diskDataSource.getDailyForecast("saigon"))
            .thenReturn(Observable.just(response))
        Mockito.verifyNoMoreInteractions(diskDataSource)

        Mockito.`when`(networkDataSource.getDailyForecast("saigon"))
            .thenReturn(Observable.just(response))
        Mockito.verifyNoMoreInteractions(networkDataSource)

        mainRepository.getDailyForecast("saigon")
        Mockito.verify(memoryDataSource).getDailyForecast("saigon")
        Mockito.verify(diskDataSource).getDailyForecast("saigon")
        Mockito.verify(networkDataSource).getDailyForecast("saigon")

        Mockito.reset(memoryDataSource)
        Mockito.reset(diskDataSource)
        Mockito.reset(networkDataSource)
    }
}