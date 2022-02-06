package com.hung.openweather.ui

import android.content.Context
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import com.hung.openweather.R
import com.hung.openweather.api.ApiManager
import com.hung.openweather.fragments.MainFragment
import com.hung.openweather.models.WeatherResponse
import com.hung.openweather.utils.Constants
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
        val sharedPrefs = context.getSharedPreferences(Constants.TimeId.TIME_ID_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().commit()
    }

    /**
     * Given Main Fragment is initialized
     * When I do nothing
     * Then edit text hint and button title shows correctly
     */
    @Test
    fun testFragmentItemName(): Unit = runBlocking {
        launchFragmentInContainer<MainFragment>(themeResId = themResId)

        onView(withId(R.id.et_place))
            .check(matches(withHint(UiTestUtils.getString(R.string.place))))

        onView(withId(R.id.btn_get_weather))
            .check(matches(withSubstring(UiTestUtils.getString(R.string.get_weather))))
    }

    /**
     * Given Main Fragment is initialized
     * When I input "saigon" to edit text field and hit get weather button
     * Then App gets data from API and shows loading view
     */
    @Test
    fun testLoading(): Unit = runBlocking {
        val emptyResponse = WeatherResponse()
        mockWebServer.enqueue(MockResponse().setBody(Gson().toJson(emptyResponse)))

        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        // Check loading dialog is shown
        onView(withText(UiTestUtils.getString(R.string.loading))).inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    /**
     * Given Main Fragment and API empty response data
     * When I input "saigon" to edit text field and hit get weather button
     * Then App shows empty result view
     */
    @Test
    fun testLoadSuccessfullyWithEmptyData(): Unit = runBlocking {
        val emptyResponse = WeatherResponse()
        mockWebServer.enqueue(MockResponse().setBody(Gson().toJson(emptyResponse)))

        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Check recycler view is empty
        onView(withId(R.id.layout_empty_result)).check(
            matches(
                CoreMatchers.`is`(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            matches(
                CoreMatchers.not(
                    isDisplayed()
                )
            )
        )
    }

    /**
     * Given Main Fragment and weather response data in 7 days
     * When I input "saigon" to edit text field and hit get weather button
     * Then App shows recycler view with 7 items
     */
    @Test
    fun testLoadSuccessfully(): Unit = runBlocking {
        mockWebServer.enqueue(MockResponse().setBody(UiTestUtils.getJsonFromResource(this, "weather_response_success.json")))

        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Check recycler view is available
        onView(withId(R.id.layout_empty_result)).check(
            matches(
                CoreMatchers.not(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            matches(
                CoreMatchers.`is`(
                    isDisplayed()
                )
            )
        )

        // Check first item date
        onView(UiTestUtils.withRecyclerView(R.id.rv_weather_forecast).atPositionOnView(0, R.id.tv_date))
            .check(matches(withSubstring("Sun, 06 Feb 2022")))

        // Check last item date of 7 titles
        onView(withId(R.id.rv_weather_forecast))
            .perform(RecyclerViewActions.scrollToPosition<WeatherViewHolder>(6))
        onView(UiTestUtils.withRecyclerView(R.id.rv_weather_forecast).atPositionOnView(6, R.id.tv_date))
            .check(matches(withSubstring("Sat, 12 Feb 2022")))

        // Verify number of items (8 rows on 7 items)
        onView(withId(R.id.rv_weather_forecast))
            .perform(RecyclerViewActions.scrollToPosition<WeatherViewHolder>(7))
        onView(UiTestUtils.withRecyclerView(R.id.rv_weather_forecast).atPosition(7))
            .check(ViewAssertions.doesNotExist())
    }

    /**
     * Given Main Fragment and API failed response
     * When I input "saigon" to edit text field and hit get weather button
     * Then App shows empty result view and Toast with error message
     */
    @Test
    fun testLoadFailed(): Unit = runBlocking {
        val string = UiTestUtils.getJsonFromResource(this, "weather_response_failed.json")
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(string))

        var activity: FragmentActivity? = null
        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            activity = fragment.requireActivity()
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Check toast is shown
        onView(withText("HTTP 400 Client Error")).inRoot(
            withDecorView(CoreMatchers.not(CoreMatchers.`is`(activity?.window?.decorView)))
        ).check(matches(isDisplayed()))

        // Check recycler view is empty
        onView(withId(R.id.layout_empty_result)).check(
            matches(
                CoreMatchers.`is`(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            matches(
                CoreMatchers.not(
                    isDisplayed()
                )
            )
        )
    }

    /**
     * Given Main Fragment with succeeded API response
     * When I re-input "saigon" to edit text field and hit get weather button but receive API failed response
     * Then App still shows recycler view with 7 items of cached succeeded API response
     */
    @Test
    fun testLoadFailedButUseCache(): Unit = runBlocking {
        // Load succeeded, save to cache
        mockWebServer.enqueue(MockResponse().setBody(UiTestUtils.getJsonFromResource(this, "weather_response_success.json")))

        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Load failed, use cache
        val string = UiTestUtils.getJsonFromResource(this, "weather_response_failed.json")
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(string))

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Check recycler view is available
        onView(withId(R.id.layout_empty_result)).check(
            matches(
                CoreMatchers.not(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            matches(
                CoreMatchers.`is`(
                    isDisplayed()
                )
            )
        )
    }

    /**
     * Given Main Fragment with succeeded empty API response
     * When I re-input "saigon" to edit text field and hit get weather button but receive API failed response
     * Then App shows empty result view and Toast with error message
     */
    @Test
    fun testLoadFailedButUseEmptyCache(): Unit = runBlocking {
        // Load succeeded but empty, save to cache
        val emptyResponse = WeatherResponse()
        mockWebServer.enqueue(MockResponse().setBody(Gson().toJson(emptyResponse)))

        var activity: FragmentActivity? = null
        launchFragmentInContainer<MainFragment>(themeResId = themResId).onFragment { fragment ->
            activity = fragment.requireActivity()
            clearRefreshDataFromApiTrigger(fragment.requireContext())
        }

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Load failed, not use cache, show error
        val string = UiTestUtils.getJsonFromResource(this, "weather_response_failed.json")
        mockWebServer.enqueue(MockResponse().setResponseCode(400).setBody(string))

        onView(withId(R.id.et_place)).perform(clearText(), typeText("saigon"))
        onView(withId(R.id.btn_get_weather)).perform(click())

        UiTestUtils.sleep(3000L)

        // Check toast is shown
        onView(withText("HTTP 400 Client Error")).inRoot(
            withDecorView(CoreMatchers.not(CoreMatchers.`is`(activity?.window?.decorView)))
        ).check(matches(isDisplayed()))

        // Check recycler view is empty
        onView(withId(R.id.layout_empty_result)).check(
            matches(
                CoreMatchers.`is`(
                    isDisplayed()
                )
            )
        )
        onView(withId(R.id.rv_weather_forecast)).check(
            matches(
                CoreMatchers.not(
                    isDisplayed()
                )
            )
        )
    }
}