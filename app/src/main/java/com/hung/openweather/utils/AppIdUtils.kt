package com.hung.openweather.utils

import android.content.Context
import android.text.TextUtils
import com.hung.openweather.App

class AppIdUtils {

    companion object {

        @Volatile
        private var _appId: String? = null

        fun storeAppId(appId: String, context: Context) {
            if (!TextUtils.isEmpty(appId)) {
                _appId = appId
            }

            SharedPreferencesManager.getInstance(context).change(
                Constants.EncryptedAppId.ENCRYPTED_APP_ID_SHARED_PREFERENCES,
                Constants.EncryptedAppId.APP_ID,
                appId
            )
        }

        fun getAppId(context: Context): String? {
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