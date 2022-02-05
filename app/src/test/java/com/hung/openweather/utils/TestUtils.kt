package com.hung.openweather.utils

import java.io.IOException
import java.nio.charset.Charset

class TestUtils {

    companion object {

        fun getJsonFromResource(obj: Any, fileName: String): String? {
            return try {
                val classLoader = obj.javaClass.classLoader
                val `is` = classLoader.getResourceAsStream(fileName)
                val size: Int = `is`.available()
                val buffer = ByteArray(size)
                `is`.read(buffer)
                `is`.close()
                String(buffer, Charset.forName("UTF-8"))
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }
        }
    }
}