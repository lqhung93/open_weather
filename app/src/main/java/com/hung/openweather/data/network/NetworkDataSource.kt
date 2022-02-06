package com.hung.openweather.data.network

import android.content.Context
import com.hung.openweather.App
import com.hung.openweather.data.DataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.AppIdUtils
import com.hung.openweather.utils.DateUtils
import com.hung.openweather.utils.testing.OpenForTesting
import io.reactivex.Observable

@OpenForTesting
class NetworkDataSource(private val context: Context) : DataSource {

    override fun getDailyForecast(query: String): Observable<WeatherResponse> =
        App.getApiManger().weatherService
            .getDailyForecast(AppIdUtils.getAppId(context), query, "7", "metric")
            .doOnNext {
                DateUtils.saveCurrentTime(context)
            }

}