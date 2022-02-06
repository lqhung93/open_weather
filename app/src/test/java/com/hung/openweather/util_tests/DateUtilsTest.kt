package com.hung.openweather.util_tests

import com.hung.openweather.utils.DateUtils
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class DateUtilsTest {

    /**
     * Given current time in seconds
     * When converting current time to date string
     * Then date string shows correctly
     */
    @Test
    fun convertSecondsToDateString() {
        Assert.assertEquals("Sun, 06 Feb 2022", DateUtils.convertSecondsToDateString(1644080400))
        Assert.assertFalse(DateUtils.convertSecondsToDateString(1644166800) == "Sun, 06 Feb 2022")
        Assert.assertFalse(DateUtils.convertSecondsToDateString(1644253200) == "Sun, 06 Feb 2022")
        Assert.assertEquals("Wed, 09 Feb 2022", DateUtils.convertSecondsToDateString(1644339600))
    }
}