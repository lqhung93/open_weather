package com.hung.openweather.viewmodels

import com.hung.openweather.App
import com.hung.openweather.R
import com.hung.openweather.models.WeatherData
import com.hung.openweather.utils.Utils
import kotlin.math.roundToInt

class WeatherViewHolderDataModel {

    val date: String
    val avgTemp: String
    val pressure: String
    val humidity: String
    val description: String

    constructor(data: WeatherData) {
        val resource = App.instance
        date = resource.getString(R.string.date, Utils.convertSecondsToDateString(data.dt ?: 0))
        avgTemp = resource.getString(R.string.average_temperature, averageTemp(data))
        pressure = resource.getString(R.string.pressure, data.pressure)
        humidity = resource.getString(R.string.humidity, data.humidity)
        description = resource.getString(R.string.description, getDescription(data))
    }

    private fun averageTemp(data: WeatherData): Int {
        val max = data.temp?.max ?: 0.0
        val min = data.temp?.min ?: 0.0
        return max.plus(min).div(2).roundToInt()
    }

    private fun getDescription(data: WeatherData): String {
        return data.weather?.firstOrNull()?.let {
            it.description
        } ?: ""
    }
}