package com.hung.openweather.models

import com.google.gson.annotations.SerializedName

data class WeatherData(
    @SerializedName("dt")
    var dt: Int? = null,

    @SerializedName("humidity")
    var humidity: Int? = null,

    @SerializedName("pressure")
    var pressure: Int? = null,

    @SerializedName("temp")
    var temp: Temp? = null,

    @SerializedName("weather")
    var weather: List<Weather>? = null
)
