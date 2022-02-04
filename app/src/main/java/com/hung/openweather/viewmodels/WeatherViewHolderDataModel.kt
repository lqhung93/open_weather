package com.hung.openweather.viewmodels

import com.hung.openweather.App
import com.hung.openweather.R
import com.hung.openweather.models.WeatherData

class WeatherViewHolderDataModel {

    val date: String
    val avgTemp: String
    val pressure: String
    val humidity: String
    val description: String

    constructor(data: WeatherData) {
        val resource = App.instance
        date = resource.getString(R.string.date, data.dt.toString())
        avgTemp = resource.getString(R.string.average_temperature, data.pressure.toString())
        pressure = resource.getString(R.string.pressure, data.pressure.toString())
        humidity = resource.getString(R.string.humidity, data.humidity.toString())
        description = resource.getString(R.string.description, data.pressure.toString())
    }
}