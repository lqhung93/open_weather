package com.hung.openweather.utils

import android.text.TextUtils
import com.hung.openweather.App
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        private val context = App.instance

        @Volatile
        private var _appId: String? = null

        fun convertSecondsToDateString(seconds: Int): String {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy")
            return sdf.format(Date(seconds * 1000L))
        }

        fun storeAppId(appId: String) {
            if (!TextUtils.isEmpty(appId)) {
                _appId = appId
            }

            SharedPreferencesManager.getInstance(context).change(
                Constants.EncryptedAppId.ENCRYPTED_APP_ID_SHARED_PREFERENCES,
                Constants.EncryptedAppId.APP_ID,
                appId
            )
        }

        fun getAppId(): String? {
            if (!TextUtils.isEmpty(_appId)) {
                return _appId
            }
            try {
                _appId = SharedPreferencesManager.getInstance(context).getString(
                    Constants.EncryptedAppId.ENCRYPTED_APP_ID_SHARED_PREFERENCES,
                    Constants.EncryptedAppId.APP_ID)
                return _appId
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return null
        }
    }
}