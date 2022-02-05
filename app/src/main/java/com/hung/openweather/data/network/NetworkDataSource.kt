package com.hung.openweather.data.network

import com.hung.openweather.App
import com.hung.openweather.data.DataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.DateUtils
import com.hung.openweather.utils.Utils
import io.reactivex.Observable

class NetworkDataSource : DataSource {

    override fun getDailyForecast(query: String): Observable<WeatherResponse> =
        App.getApiManger().weatherService
            .getDailyForecast(Utils.getAppId(), query, "7", "metric")
            .doOnNext {
                DateUtils.saveCurrentTime()
            }

}