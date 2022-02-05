package com.hung.openweather.data.disk

import android.content.Context
import com.google.gson.Gson
import com.hung.openweather.data.DataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.Constants
import com.hung.openweather.utils.DateUtils
import com.hung.openweather.utils.SharedPreferencesManager
import com.hung.openweather.utils.testing.OpenForTesting
import io.reactivex.Observable

@OpenForTesting
class DiskDataSource(private val context: Context) : DataSource {

    private val preferences = SharedPreferencesManager.getInstance(context)

    override fun getDailyForecast(query: String): Observable<WeatherResponse> =
        Observable.create { emitter ->
            val encryptedQuery = preferences.encryptionUtils.encrypt(query.toLowerCase())
            val data = preferences.getString(Constants.WeatherDataId.WEATHER_DATA_ID_SHARED_PREFERENCES, encryptedQuery)
            var weatherResponse: WeatherResponse? = null
            try {
                weatherResponse = Gson().fromJson(data, WeatherResponse::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
            if (!DateUtils.needToRefreshDataFromApi(context) && weatherResponse != null) {
                emitter.onNext(weatherResponse)
            }
            emitter.onComplete()
        }

    fun saveData(query: String, data: WeatherResponse?) {
        if (data != null) {
            val encryptedQuery = preferences.encryptionUtils.encrypt(query.toLowerCase())
            preferences.change(Constants.WeatherDataId.WEATHER_DATA_ID_SHARED_PREFERENCES, encryptedQuery, Gson().toJson(data))
        }
    }
}