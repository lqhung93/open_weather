package com.hung.openweather.api

import com.hung.openweather.models.WeatherResponse
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("forecast/daily")
    fun getDailyForecast(
        @Query("appid") appid: String? = null,
        @Query("q") q: String? = null,
        @Query("cnt") cnt: String? = null,
        @Query("units") units: String? = null,
    ): Observable<WeatherResponse>
    
}