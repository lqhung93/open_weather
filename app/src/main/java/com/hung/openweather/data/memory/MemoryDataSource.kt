package com.hung.openweather.data.memory

import com.hung.openweather.data.DataSource
import com.hung.openweather.models.WeatherResponse
import io.reactivex.Observable

class MemoryDataSource : DataSource {

    private var data: WeatherResponse? = null
    private var query: String? = null

    override fun getDailyForecast(query: String): Observable<WeatherResponse> =
        Observable.create { emitter ->
            if (query == this.query && data != null) {
                emitter.onNext(data!!)
            }
            emitter.onComplete()
        }

    fun saveData(query: String, data: WeatherResponse) {
        this.query = query
        this.data = data
    }

}