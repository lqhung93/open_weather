package com.hung.openweather.utils

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EncryptionUtilsTest {

    @get:Rule
    private val instrumentationContext = InstrumentationRegistry.getInstrumentation().context

    /**
     * Given data
     * When encrypting successfully
     * Then decrypted data is the same as given value
     */
    @Test
    fun testEncryptAndDecryptData() {
        val data = "hello_world"
        val encrypted = EncryptionUtils.getInstance(instrumentationContext).encrypt(data)
        Assert.assertEquals(data, EncryptionUtils.getInstance(instrumentationContext).decrypt(encrypted))
    }
}