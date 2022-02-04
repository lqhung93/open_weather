package com.hung.openweather.models

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    @SerializedName("list")
    var list: List<WeatherData>? = null,
)