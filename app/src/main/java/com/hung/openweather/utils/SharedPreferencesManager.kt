package com.hung.openweather.utils

import android.content.Context
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReadWriteLock
import java.util.concurrent.locks.ReentrantReadWriteLock

class SharedPreferencesManager private constructor(private val context: Context) {

    private val readWriteLock: ReadWriteLock = ReentrantReadWriteLock()
    private val readLock: Lock = readWriteLock.readLock()
    private val writeLock: Lock = readWriteLock.writeLock()

    private val encryptionUtils = EncryptionUtils.getInstance(context)

    fun change(settingsKey: String, key: String, value: String?) {
        writeLock.lock()
        try {
            val settings = context.getSharedPreferences(settingsKey, Context.MODE_PRIVATE)
            val editor = settings.edit()
            if (value != null) {
                val encrypted = encryptionUtils.encrypt(value)
                editor.putString(key, encrypted)
            } else {
                editor.remove(key)
            }
            editor.commit()
        } finally {
            writeLock.unlock()
        }
    }

    fun getString(settingsKey: String, key: String, defaultValue: String?): String? {
        readLock.lock()
        return try {
            if (context == null) {
                return null
            }
            val settings = context.getSharedPreferences(settingsKey, Context.MODE_PRIVATE)
            val encrypted = settings.getString(key, defaultValue)
            if (encrypted != null) {
                try {
                    encryptionUtils.decrypt(encrypted)
                } catch (e : Exception) {
                    defaultValue
                }
            } else {
                defaultValue
            }
        } finally {
            readLock.unlock()
        }
    }

    companion object {

        private var instance: SharedPreferencesManager? = null
        fun getInstance(context: Context): SharedPreferencesManager {
            if (instance == null) {
                instance = SharedPreferencesManager(context)
            }
            return instance!!
        }
    }
}