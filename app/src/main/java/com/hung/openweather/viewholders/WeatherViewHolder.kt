package com.hung.openweather.viewholders

import androidx.recyclerview.widget.RecyclerView
import com.hung.openweather.databinding.ListItemWeatherBinding
import com.hung.openweather.models.WeatherData
import com.hung.openweather.viewmodels.WeatherViewHolderViewModel

class WeatherViewHolder(private val binding: ListItemWeatherBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(weatherData: WeatherData) {
        binding.viewModel = WeatherViewHolderViewModel(weatherData)
    }
}