package com.hung.openweather

import android.app.Application
import com.hung.openweather.api.ApiManager
import com.hung.openweather.utils.Utils

class App : Application() {

    companion object {

        lateinit var instance: App
            private set

        @Volatile
        private var sApiManager: ApiManager? = null

        /**
         * Lazy initialisation of an Api Manager.
         *
         * @return ApiManager
         */
        @Synchronized
        fun getApiManger(): ApiManager {
            if (sApiManager == null) {
                synchronized(App::class.java) {
                    if (sApiManager == null) {
                        sApiManager = ApiManager()
                        sApiManager?.init(instance)
                    }
                }
            }
            return sApiManager!!
        }
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Utils.storeAppId(BuildConfig.APP_ID)
    }

}
