package com.gramaurja.data

import android.content.Context
import android.content.SharedPreferences

object UserPreferences {

    private const val PREF_NAME    = "grama_urja_prefs"
    private const val KEY_NAME     = "farmer_name"
    private const val KEY_REG      = "is_registered"

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    fun saveFarmerName(context: Context, name: String) {
        prefs(context).edit()
            .putString(KEY_NAME, name.trim())
            .putBoolean(KEY_REG, true)
            .apply()
    }

    fun getFarmerName(context: Context): String =
        prefs(context).getString(KEY_NAME, "Farmer") ?: "Farmer"

    fun isRegistered(context: Context): Boolean =
        prefs(context).getBoolean(KEY_REG, false)

    fun clearData(context: Context) {
        prefs(context).edit().clear().apply()
    }
}