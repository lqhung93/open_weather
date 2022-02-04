package com.hung.openweather.adapters

import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.hung.openweather.viewholders.WeatherViewHolder

import androidx.databinding.DataBindingUtil

import android.view.LayoutInflater
import com.google.gson.Gson
import com.hung.openweather.R
import com.hung.openweather.databinding.ListItemWeatherBinding
import com.hung.openweather.models.WeatherData

class WeatherAdapter : RecyclerView.Adapter<WeatherViewHolder>() {

    private val weatherDataList: ArrayList<WeatherData> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WeatherViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ListItemWeatherBinding = DataBindingUtil.inflate(inflater, R.layout.list_item_weather, parent, false)
        return WeatherViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WeatherViewHolder, position: Int) {
        holder.bind(weatherDataList[position])
    }

    override fun getItemCount(): Int {
        return weatherDataList.size
    }

    fun setData(data: List<WeatherData>) {
        weatherDataList.clear()
        weatherDataList.addAll(data)
        notifyDataSetChanged()
    }
}