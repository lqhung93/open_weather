package com.hung.openweather.utils

import android.content.Context
import android.content.SharedPreferences
import android.text.TextUtils
import com.hung.openweather.App
import java.text.SimpleDateFormat
import java.util.*

class Utils {

    companion object {

        private const val ENCRYPTED_APP_ID = "encrypted_app_id"
        const val SHARED_PREFERENCE_NAME = "open_weather"
        private val LOCK = Any()

        private val context = App.instance

        @Volatile
        private var _appId: String? = null

        fun convertSecondsToDateString(seconds: Int): String {
            val sdf = SimpleDateFormat("EEE, dd MMM yyyy")
            return sdf.format(Date(seconds * 1000L))
        }

        @Throws(Exception::class)
        private fun encryptAndSaveAppId(encryptionUtil: EncryptionUtils, editor: SharedPreferences.Editor, appId: String) {
            val encryptedAppId: String = encryptionUtil.encrypt(appId)
            editor.putString(ENCRYPTED_APP_ID, encryptedAppId)
            editor.commit()
        }

        fun storeAppId(appId: String) {
            synchronized(LOCK) {
                if (!TextUtils.isEmpty(appId)) {
                    _appId = appId
                }
                val pref: SharedPreferences = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                val editor = pref.edit()
                val encryptionUtil: EncryptionUtils = EncryptionUtils.getInstance(context)
                try {
                    encryptAndSaveAppId(encryptionUtil, editor, appId)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }

        fun getAppId(): String? {
            synchronized(LOCK) {
                if (!TextUtils.isEmpty(_appId)) {
                    return _appId
                }
                val pref = context.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE)
                val encryptionUtil = EncryptionUtils.getInstance(context)
                val encrypted = pref.getString(ENCRYPTED_APP_ID, null) ?: return null
                try {
                    _appId = encryptionUtil.decrypt(encrypted)
                    return _appId
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                return null
            }
        }
    }
}