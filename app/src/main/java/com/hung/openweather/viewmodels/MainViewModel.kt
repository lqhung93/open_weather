package com.hung.openweather.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.repository.MainRepository
import io.reactivex.Single

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    fun getDailyForecast(query: String): Single<WeatherResponse> {
        return repository.getDailyForecast(query)
    }

    companion object {

        fun builder(context: Context): ViewModelProvider.Factory {
            val repository = MainRepository()
            return MainViewModelFactory(repository)
        }
    }

}