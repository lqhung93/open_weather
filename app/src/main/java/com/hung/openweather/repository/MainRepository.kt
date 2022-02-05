package com.hung.openweather.repository

import com.hung.openweather.App
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.Utils
import io.reactivex.Single

class MainRepository {

    fun getDailyForecast(query: String): Single<WeatherResponse> {
        return App.getApiManger().weatherService.getDailyForecast(
            Utils.getAppId(),
            query,
            "7",
            "metric"
        )
    }
}