package com.hung.openweather.models

import com.google.gson.annotations.SerializedName

data class Weather(
    @SerializedName("description")
    var description: String? = null
)