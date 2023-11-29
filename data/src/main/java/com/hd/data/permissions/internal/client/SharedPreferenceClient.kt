package com.hd.data.permissions.internal.client

import android.content.Context
import androidx.preference.PreferenceManager
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
internal class SharedPreferenceClient @Inject constructor(appContext: Context) {

    private val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(appContext)
    fun getPreference(preferenceKey: String, defaultValue: Any): Any {
        return when (defaultValue) {
            is String -> sharedPreferences.getString(preferenceKey, defaultValue) ?: defaultValue
            is Boolean -> sharedPreferences.getBoolean(preferenceKey, defaultValue)
            is Int -> sharedPreferences.getInt(preferenceKey, defaultValue)
            // Add more types as needed
            else -> throw IllegalArgumentException("Unsupported preference type")
        }
    }

    fun setPreference(preferenceKey: String, value: Any) {
        sharedPreferences.edit().apply {
            when (value) {
                is String -> putString(preferenceKey, value)
                is Boolean -> putBoolean(preferenceKey, value)
                is Int -> putInt(preferenceKey, value)
                // Add more types as needed
                else -> throw IllegalArgumentException("Unsupported preference type")
            }
        }.apply()
    }

    fun clearPreference(preferenceKey: String) {
        sharedPreferences.edit().remove(preferenceKey).apply()
    }
}

