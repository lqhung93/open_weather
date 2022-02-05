package com.hung.openweather.viewmodels

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.hung.openweather.data.disk.DiskDataSource
import com.hung.openweather.data.memory.MemoryDataSource
import com.hung.openweather.data.network.NetworkDataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.repository.MainRepository
import com.hung.openweather.utils.SharedPreferencesManager
import io.reactivex.Observable

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    fun getDailyForecast(query: String): Observable<WeatherResponse> {
        return repository.getDailyForecast(query)
    }

    companion object {

        fun builder(context: Context): ViewModelProvider.Factory {
            val memoryDataSource = MemoryDataSource()
            val diskDataSource = DiskDataSource(SharedPreferencesManager.getInstance(context))
            val networkDataSource = NetworkDataSource()

            val repository = MainRepository(memoryDataSource, diskDataSource, networkDataSource)
            return MainViewModelFactory(repository)
        }
    }

}