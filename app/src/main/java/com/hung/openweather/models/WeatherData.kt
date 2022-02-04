package com.hung.openweather.models

import androidx.databinding.Bindable
import androidx.databinding.Observable
import com.google.gson.annotations.SerializedName

data class WeatherData(
    @Bindable
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
) : Observable {

    override fun addOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

    override fun removeOnPropertyChangedCallback(callback: Observable.OnPropertyChangedCallback?) {

    }

}