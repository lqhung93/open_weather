package com.hung.openweather.data.memory

import android.content.Context
import com.hung.openweather.data.DataSource
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.DateUtils
import com.hung.openweather.utils.testing.OpenForTesting
import io.reactivex.Observable

@OpenForTesting
class MemoryDataSource(private val context: Context) : DataSource {

    private var data: WeatherResponse? = null
    private var query: String? = null

    override fun getDailyForecast(query: String): Observable<WeatherResponse> =
        Observable.create { emitter ->
            if (!DateUtils.needToRefreshDataFromApi(context) &&
                query.equals(this.query, true) &&
                (data != null && !data?.list.isNullOrEmpty())
            ) {
                emitter.onNext(data!!)
            }
            emitter.onComplete()
        }

    fun saveData(query: String, data: WeatherResponse) {
        this.query = query
        this.data = data
    }

}