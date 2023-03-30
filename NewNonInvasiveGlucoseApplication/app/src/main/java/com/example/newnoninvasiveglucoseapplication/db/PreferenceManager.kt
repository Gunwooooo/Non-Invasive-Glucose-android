package com.example.newnoninvasiveglucoseapplication.db

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {
    private val _prefs: SharedPreferences = context.getSharedPreferences("_prefs_name", Context.MODE_PRIVATE)

    fun getString(key: String, defValue: String): String {
        return _prefs.getString(key, defValue).toString()
    }

    fun setString(key: String, string: String) {
        _prefs.edit().putString(key, string).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return _prefs.getBoolean(key, defValue)
    }

    fun setBoolean(key: String, boolean: Boolean) {
        _prefs.edit().putBoolean(key, boolean).apply()
    }
}

