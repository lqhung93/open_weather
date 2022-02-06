package com.hung.openweather.utils

class Constants {

    object EncryptedAppId {
        const val ENCRYPTED_APP_ID_SHARED_PREFERENCES = "encrypted_app_id_shared_preferences"
        const val APP_ID = "app_id"
    }

    object WeatherDataId {
        const val WEATHER_DATA_ID_SHARED_PREFERENCES = "weather_data_id_shared_preferences"
    }

    object TimeId {
        const val TIME_ID_SHARED_PREFERENCES = "time_id_shared_preferences"
        const val TIME_ID = "time_id"
    }

    object ApiState {
        const val STARTED = "STARTED"
        const val COMPLETED = "COMPLETED"
        const val ERROR = "ERROR"
    }
}