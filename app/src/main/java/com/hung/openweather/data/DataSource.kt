package com.hung.openweather.data

import com.hung.openweather.models.WeatherResponse
import io.reactivex.Observable

interface DataSource {

    fun getDailyForecast(query: String): Observable<WeatherResponse>
}