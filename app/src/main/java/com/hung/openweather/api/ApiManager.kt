package com.hung.openweather.api

import android.content.Context
import androidx.annotation.VisibleForTesting
import com.hung.openweather.BuildConfig
import okhttp3.Cache
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.internal.platform.Platform
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

class ApiManager {

    companion object {

        private const val END_POINT = "https://api.openweathermap.org/data"
        private const val API_VERSION = "/2.5"

        internal var BASE_URL = "$END_POINT$API_VERSION/"
    }

    private lateinit var apiAdapter: Retrofit
    lateinit var weatherService: WeatherService
        private set

    fun init(context: Context) {
        initRetrofit(context)
        initService()
    }

    private fun initService() {
        weatherService = apiAdapter.create(WeatherService::class.java)
    }

    private fun initRetrofit(context: Context) {
        val builder = getOkHttpBuilder(getCache(context))

        if (BuildConfig.DEBUG) {
            val interceptor = HttpLoggingInterceptor {
                Platform.get().log(Platform.INFO, it, null)
            }
            interceptor.level = HttpLoggingInterceptor.Level.BODY
            builder.addInterceptor(interceptor)
        }

        apiAdapter = Retrofit.Builder()
            .baseUrl(HttpUrl.parse(BASE_URL)!!)
            .client(builder.build())
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
    }

    private fun getCache(context: Context): Cache {
        val sizeOfCache = (10 * 1024 * 1024).toLong() // 10 MB
        return Cache(File(context.cacheDir, "https"), sizeOfCache)
    }

    private fun getOkHttpBuilder(cache: Cache): OkHttpClient.Builder {
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .cache(cache)
    }
}