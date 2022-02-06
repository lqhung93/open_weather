package com.hung.openweather.util_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hung.openweather.utils.AppIdUtils
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AppIdUtilsTest {

    @get:Rule
    private val instrumentationContext = InstrumentationRegistry.getInstrumentation().targetContext

    /**
     * Given app Id value
     * When store to shared preferences successfully
     * Then retrieve app Id with the same as given value
     */
    @Test
    fun testSaveAndRetrieveAppId() {
        val testAppId = "test_app_id"
        AppIdUtils.storeAppId(testAppId, instrumentationContext)
        Assert.assertEquals(testAppId, AppIdUtils.getAppId(instrumentationContext))
    }
}