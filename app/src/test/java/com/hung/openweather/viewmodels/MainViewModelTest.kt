package com.hung.openweather.viewmodels

import com.hung.openweather.repository.MainRepository
import com.hung.openweather.utils.mock
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.powermock.api.mockito.PowerMockito

@RunWith(JUnit4::class)
class MainViewModelTest {

    private val mainRepository = PowerMockito.mock(MainRepository::class.java)
    private val mainViewModel = PowerMockito.spy(MainViewModel(mainRepository))

    @Test
    fun testNull() {
        Mockito.verify(mainViewModel, Mockito.never()).getDailyForecast("saigon")
    }

    @Test
    fun testGetDailyForecast() {
        Mockito.`when`(mainRepository.getDailyForecast("saigon")).thenReturn(mock())
        Mockito.verifyNoMoreInteractions(mainRepository)

        mainViewModel.getDailyForecast("saigon")
        Mockito.verify(mainRepository).getDailyForecast("saigon")

        Mockito.reset(mainRepository)

        mainViewModel.getDailyForecast("")
        Mockito.verify(mainRepository).getDailyForecast("")

        Mockito.reset(mainRepository)
    }
}