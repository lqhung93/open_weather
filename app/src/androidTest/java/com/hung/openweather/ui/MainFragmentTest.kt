package com.hung.openweather.ui

import android.content.Context
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.hung.openweather.R
import com.hung.openweather.api.ApiManager
import com.hung.openweather.fragments.MainFragment
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.UiTestUtils
import com.hung.openweather.viewholders.WeatherViewHolder
import kotlinx.coroutines.runBlocking
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainFragmentTest {

    @get:Rule
    private val themResId = R.style.Theme_MaterialComponents
    private val mockWebServer: MockWebServer = MockWebServer()

    @Before
    fun setUp() {
        mockWebServer.start(9999)
        ApiManager.BASE_URL = mockWebServer.url("/").toString()
    }

    @After
    fun tearDown() {
        mockWebServer.shutdown()
    }

    private fun clearRefreshDataFromApiTrigger(context: Context) {
        val sharedPrefs = context.getSharedPreferences("time_id_shared_preferences", Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().commit()
    }

    @Test
    fun testFragmentItemName(): Unit = runBlocking {
        launchFragmentInContainer<MainFragment>(themeResId = themResId)

        onView(withId(R.id.et_place))
            .check(ViewAssertions.matches(ViewMatchers.withHint(UiTestUtils.getString(R.string.place))))

        onView(withId(R.id.btn_get_weather))
            .check(ViewAssertions.matches(ViewMatchers.withSubstring(UiTestUtils.getString(R.string.get_weather))))
    }

    @Test
    fun testEmptyData(): Unit = runBlocking {
        val emptyResponse = WeatherResponse()
        mockWebServer.enqueue(MockResponse().setBody(Gson().toJson(emptyResponse)))

        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        onView(withId(R.id.layout_empty_result)).check(
            ViewAssertions.matches(
                CoreMatchers.`is`(
                    ViewMatchers.isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            ViewAssertions.matches(
                CoreMatchers.not(
                    ViewMatchers.isDisplayed()
                )
            )
        )
    }

    @Test
    fun testLoadSuccessfully(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody(UiTestUtils.getJsonFromResource(this, "weather_response_success.json")))

        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Check recycler view is available
        onView(withId(R.id.layout_empty_result)).check(
            ViewAssertions.matches(
                CoreMatchers.not(
                    ViewMatchers.isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            ViewAssertions.matches(
                CoreMatchers.`is`(
                    ViewMatchers.isDisplayed()
                )
            )
        )

        // Check first item date
        onView(UiTestUtils.withRecyclerView(R.id.rv_weather_forecast).atPositionOnView(0, R.id.tv_date))
            .check(ViewAssertions.matches(ViewMatchers.withSubstring("Sun, 06 Feb 2022")))

        // Check last item date of 7 titles
        onView(withId(R.id.rv_weather_forecast))
            .perform(RecyclerViewActions.scrollToPosition<WeatherViewHolder>(6))
        onView(UiTestUtils.withRecyclerView(R.id.rv_weather_forecast).atPositionOnView(6, R.id.tv_date))
            .check(ViewAssertions.matches(ViewMatchers.withSubstring("Sat, 12 Feb 2022")))

        // Verify number of items (8 rows on 7 items)
        onView(withId(R.id.rv_weather_forecast))
            .perform(RecyclerViewActions.scrollToPosition<WeatherViewHolder>(7))
        onView(UiTestUtils.withRecyclerView(R.id.rv_weather_forecast).atPosition(7))
            .check(ViewAssertions.doesNotExist())
    }
}