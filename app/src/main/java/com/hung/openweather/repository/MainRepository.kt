package com.hung.openweather.repository

import com.hung.openweather.data.DataSource
import com.hung.openweather.data.disk.DiskDataSource
import com.hung.openweather.data.memory.MemoryDataSource
import com.hung.openweather.data.network.NetworkDataSource
import com.hung.openweather.models.WeatherResponse
import io.reactivex.Observable

class MainRepository(
    private val memoryDataSource: MemoryDataSource,
    private val diskDataSource: DiskDataSource,
    private val networkDataSource: NetworkDataSource
) : DataSource {

    override fun getDailyForecast(query: String): Observable<WeatherResponse> {
        val memory: Observable<WeatherResponse> = getDataFromMemory(query)
        val disk: Observable<WeatherResponse> = getDataFromDiskCached(query)
        val network: Observable<WeatherResponse> = getDataFromNetwork(query)
        return Observable
            .concat(memory, disk, network)
            .firstElement()
            .toObservable()
    }

    private fun getDataFromNetwork(query: String): Observable<WeatherResponse> {
        return networkDataSource.getDailyForecast(query)
            .doOnNext { data ->
                diskDataSource.saveData(query, data)
                memoryDataSource.saveData(query, data)
            }
    }

    private fun getDataFromDiskCached(query: String): Observable<WeatherResponse> {
        return diskDataSource.getDailyForecast(query)
            .doOnNext { data -> memoryDataSource.saveData(query, data) }
    }

    private fun getDataFromMemory(query: String): Observable<WeatherResponse> {
        return memoryDataSource.getDailyForecast(query)
    }
}