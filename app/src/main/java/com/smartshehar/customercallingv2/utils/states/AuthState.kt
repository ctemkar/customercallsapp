package com.smartshehar.customercallingv2.utils.states

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log

class AuthState(val app: Application) {

    val AUTH_DATA = "auth_data"
    val TOKEN = "token"
    val USER_TYPE = "user_type"
    private val TAG = "AuthState"

    fun saveCurrentUserToken(token: String) {
        val prefs = app.getSharedPreferences(AUTH_DATA, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(TOKEN, token)
        editor.apply()
    }

    fun isUserLoggedIn(): Boolean {
        val prefs = app.getSharedPreferences(AUTH_DATA, MODE_PRIVATE)
        return prefs.contains(TOKEN)
    }

    fun getCurrentUserType(): String {
        val prefs = app.getSharedPreferences(AUTH_DATA, MODE_PRIVATE)
        return prefs.getString(USER_TYPE, "")!!
    }

    fun getCurrentUserToken(): String {
        val prefs = app.getSharedPreferences(AUTH_DATA, MODE_PRIVATE)
        return prefs.getString(TOKEN, "")!!
    }


    fun clearLoginState(){
        val prefs = app.getSharedPreferences(AUTH_DATA, MODE_PRIVATE)
        val editor = prefs.edit()
        editor.remove(TOKEN)
        editor.remove(USER_TYPE)
        editor.apply()
    }


}