package com.hung.openweather.viewmodels

import com.google.gson.Gson
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.repository.MainRepository
import com.hung.openweather.utils.TestUtils
import io.reactivex.Observable
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito

@RunWith(JUnit4::class)
class MainViewModelTest {

    private val mainRepository = PowerMockito.mock(MainRepository::class.java)
    private val mainViewModel = PowerMockito.spy(MainViewModel(mainRepository))

    /**
     * Given nothing
     * When calling getDailyForecast in MainViewModel
     * Then nothing is called
     */
    @Test
    fun testNull() {
        Mockito.verify(mainViewModel, Mockito.never()).getDailyForecast("saigon")
    }

    /**
     * Given MainRepository
     * When calling getDailyForecast in MainViewModel
     * Then getDailyForecast in MainRepository must be called
     */
    @Test
    fun testGetDailyForecast() {
        val failure = TestUtils.getJsonFromResource(this, "weather_response_success.json")
        val response = Gson().fromJson(failure, WeatherResponse::class.java)

        Mockito.`when`(mainRepository.getDailyForecast("saigon")).thenReturn(Observable.just(response))
        Mockito.verifyNoMoreInteractions(mainRepository)

        mainViewModel.getDailyForecast("saigon")
        Mockito.verify(mainRepository).getDailyForecast("saigon")

        Mockito.reset(mainRepository)

        Mockito.`when`(mainRepository.getDailyForecast("")).thenReturn(Observable.just(response))
        Mockito.verifyNoMoreInteractions(mainRepository)

        mainViewModel.getDailyForecast("")
        Mockito.verify(mainRepository).getDailyForecast("")

        Mockito.reset(mainRepository)
    }
}