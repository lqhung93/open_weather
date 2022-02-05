package com.hung.openweather.api

import com.google.gson.Gson
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.TestUtils
import io.reactivex.observers.DisposableObserver
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.json.JSONObject
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

@RunWith(JUnit4::class)
class WeatherServiceTest {

    private lateinit var service: WeatherService
    private lateinit var mockWebServer: MockWebServer

    @Before
    fun createService() {
        mockWebServer = MockWebServer()
        service = Retrofit.Builder()
            .baseUrl(mockWebServer.url("/"))
            .addConverterFactory(GsonConverterFactory.create())
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .build()
            .create(WeatherService::class.java)
    }

    @After
    fun stopService() {
        mockWebServer.shutdown()
    }

    @Test
    fun getWeatherSucceeded() {
        val string = TestUtils.getJsonFromResource(this, "weather_response_success.json")
        val obj = Gson().fromJson(string, WeatherResponse::class.java)

        mockWebServer.enqueue(MockResponse().setBody(string))

        service.getDailyForecast("123", "saigon", "7", "metric")
            .subscribeWith(object : DisposableObserver<WeatherResponse>() {

                override fun onNext(t: WeatherResponse) {
                    Assert.assertThat(t.list?.size, CoreMatchers.`is`(7))
                    Assert.assertThat(obj, CoreMatchers.`is`(t))
                }

                override fun onComplete() {

                }

                override fun onError(e: Throwable) {

                }
            })

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/forecast/daily?appid=123&q=saigon&cnt=7&units=metric"))
    }

    @Test
    fun getWeatherFailed() {
        val string = TestUtils.getJsonFromResource(this, "weather_response_failed.json")
        mockWebServer.enqueue(MockResponse().setBody(string))
        mockWebServer.enqueue(MockResponse().setHttp2ErrorCode(400))

        service.getDailyForecast("123", "saigon", "7", "metric")
            .subscribeWith(object : DisposableObserver<WeatherResponse>() {

                override fun onNext(t: WeatherResponse) {

                }

                override fun onComplete() {

                }

                override fun onError(e: Throwable) {
                    Assert.assertThat(e.localizedMessage, CoreMatchers.`is`(400))
                }
            })

        val request = mockWebServer.takeRequest()
        Assert.assertThat(request.path, CoreMatchers.`is`("/forecast/daily?appid=123&q=saigon&cnt=7&units=metric"))
    }
}