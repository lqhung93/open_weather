package com.hung.openweather.data.disk

import com.google.gson.Gson
import com.hung.openweather.data.DataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.Constants
import com.hung.openweather.utils.SharedPreferencesManager
import io.reactivex.Observable

class DiskDataSource(private val preferences: SharedPreferencesManager) : DataSource {

    override fun getDailyForecast(query: String): Observable<WeatherResponse> =
        Observable.create { emitter ->
            val data = preferences.getString(Constants.WeatherDataId.WEATHER_DATA_ID_SHARED_PREFERENCES, query)
            var weatherResponse: WeatherResponse? = null
            try {
                weatherResponse = Gson().fromJson(data, WeatherResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (weatherResponse != null) {
                emitter.onNext(weatherResponse)
            }
            emitter.onComplete()
        }

    fun saveData(query: String, data: WeatherResponse?) {
        if (data != null) {
            preferences.change(Constants.WeatherDataId.WEATHER_DATA_ID_SHARED_PREFERENCES, query, Gson().toJson(data))
        }
    }
}