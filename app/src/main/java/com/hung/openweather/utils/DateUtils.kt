package com.hung.openweather.utils

import android.content.Context
import com.hung.openweather.App
import java.text.SimpleDateFormat
import java.util.*

class DateUtils {

    companion object {

        private const val REFRESH_THRESHOLD = 2 * 60 * 60 // 2 hours

        fun convertSecondsToDateString(seconds: Int): String {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy")
            return sdf.format(Date(seconds * 1000L))
        }

        fun saveCurrentTime(context: Context) {
            val current = System.currentTimeMillis() / 1000L
            SharedPreferencesManager.getInstance(context).change(
                Constants.TimeId.TIME_ID_SHARED_PREFERENCES,
                Constants.TimeId.TIME_ID,
                current.toString()
            )
        }

        fun needToRefreshDataFromApi(context: Context): Boolean {
            val current = System.currentTimeMillis() / 1000L
            val last = SharedPreferencesManager.getInstance(context).getString(
                Constants.TimeId.TIME_ID_SHARED_PREFERENCES,
                Constants.TimeId.TIME_ID,
            )?.toIntOrNull()

            return if (last == null) {
                true
            } else {
                current - last > REFRESH_THRESHOLD
            }
        }
    }
}