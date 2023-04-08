package com.smartshehar.customercallingv2.utils.states

import android.app.Application
import android.content.Context
import android.util.Log

class RestaurantState(val app: Application) {

    val CURRENT_RESTAURANT_ID = ""
    val RESTAURANT_DATA = "restaurant_data"

    private  val TAG = "RestaurantState"

    fun getCurrentRestaurantId(): String {
        val prefs = app.getSharedPreferences(RESTAURANT_DATA, Context.MODE_PRIVATE)
        val restaurantId = prefs.getString(CURRENT_RESTAURANT_ID, "")!!
        Log.d(TAG, "getCurrentRestaurantId: $restaurantId " )
        return restaurantId
    }

    fun saveCurrentRestaurantId(restaurantId: String) {
        val prefs = app.getSharedPreferences(RESTAURANT_DATA, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(CURRENT_RESTAURANT_ID, restaurantId)
        editor.apply()
    }
}