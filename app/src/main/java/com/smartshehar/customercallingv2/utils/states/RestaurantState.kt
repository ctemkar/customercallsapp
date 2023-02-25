package com.smartshehar.customercallingv2.utils.states

import android.app.Application
import android.content.Context

class RestaurantState(val app: Application) {

    val CURRENT_RESTAURANT_ID = ""
    val RESTAURANT_DATA = "restaurant_data"

    fun getCurrentRestaurantId(): String {
        val prefs = app.getSharedPreferences(RESTAURANT_DATA, Context.MODE_PRIVATE)
        return prefs.getString(CURRENT_RESTAURANT_ID, "")!!
    }

    fun saveCurrentRestaurantId(restaurantId: String) {
        val prefs = app.getSharedPreferences(RESTAURANT_DATA, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(CURRENT_RESTAURANT_ID, restaurantId)
        editor.apply()
    }
}