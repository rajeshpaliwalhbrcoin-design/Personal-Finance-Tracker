package com.example.data

import android.content.Context
import android.content.SharedPreferences

class SettingsManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("finance_settings", Context.MODE_PRIVATE)

    fun getDailyLimit(): Double {
        return prefs.getFloat("daily_limit", 230f).toDouble()
    }

    fun setDailyLimit(limit: Double) {
        prefs.edit().putFloat("daily_limit", limit.toFloat()).apply()
    }

    // Stores "1" for 1st of month, "26" for 26th of month etc. 
    fun getMonthStartDay(): Int {
        return prefs.getInt("month_start_day", 1)
    }

    fun setMonthStartDay(day: Int) {
        prefs.edit().putInt("month_start_day", day).apply()
    }
}
