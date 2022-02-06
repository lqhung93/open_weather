package com.hung.openweather.util_tests

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.hung.openweather.utils.SharedPreferencesManager
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SharedPreferencesManagerTest {

    @get:Rule
    private val instrumentationContext = InstrumentationRegistry.getInstrumentation().context

    /**
     * Given data
     * When store to shared preferences successfully
     * Then retrieve data with the same as given value
     */
    @Test
    fun testSaveAndRetrieveInSharedPreferences() {
        val settingsKey = "settings_key"
        val key = "key"
        val value = "hello_world"

        SharedPreferencesManager.getInstance(instrumentationContext).change(settingsKey, key, value)
        Assert.assertEquals(
            value,
            SharedPreferencesManager.getInstance(instrumentationContext).getString(settingsKey, key)
        )
    }
}