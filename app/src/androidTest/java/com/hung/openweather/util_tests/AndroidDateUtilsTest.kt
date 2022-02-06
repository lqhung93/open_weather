package com.hung.openweather.util_tests

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hung.openweather.utils.Constants
import com.hung.openweather.utils.DateUtils
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AndroidDateUtilsTest {

    @get:Rule
    private val instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext

    @Before
    fun setUp() {
        val sharedPrefs = instrumentationContext.getSharedPreferences(Constants.TimeId.TIME_ID_SHARED_PREFERENCES, Context.MODE_PRIVATE)
        sharedPrefs.edit().clear().commit()
    }

    /**
     * Given the application
     * When API isn't called
     * Then allow to call API
     */
    @Test
    fun checkNeedToRefreshDataAtFirstTime() {
        Assert.assertEquals(true, DateUtils.needToRefreshDataFromApi(instrumentationContext))
    }

    /**
     * Given the application
     * When API is called
     * Then deny to call API
     */
    @Test
    fun checkNeedToRefreshDataAfterLoadingApi() {
        DateUtils.saveCurrentTime(instrumentationContext)
        Assert.assertEquals(false, DateUtils.needToRefreshDataFromApi(instrumentationContext))
    }
}