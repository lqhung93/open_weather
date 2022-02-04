package com.hung.openweather.utils

import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        fun convertSecondsToDateString(seconds: Int): String {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy")
            return sdf.format(Date(seconds * 1000L))
        }
    }
}