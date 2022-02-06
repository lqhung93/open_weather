package com.hung.openweather.api

import com.google.gson.Gson
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.TestUtils
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class WeatherResponseTest {

    /**
     * Given failed json response
     * When parsing data from it to object
     * Then the properties in object are the same with response
     */
    @Test
    fun failure() {
        val failure = TestUtils.getJsonFromResource(this, "weather_response_failed.json")
        val jsonObject = JSONObject(failure)
        Assert.assertEquals("400", jsonObject.getString("cod"))
    }

    /**
     * Given succeeded json response
     * When parsing data from it to object
     * Then the properties in object are the same with response
     */
    @Test
    fun success() {
        val failure = TestUtils.getJsonFromResource(this, "weather_response_success.json")
        val response = Gson().fromJson(failure, WeatherResponse::class.java)
        Assert.assertEquals(7, response.list?.size)
    }
}