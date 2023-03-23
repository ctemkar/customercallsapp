package com.smartshehar.customercallingv2.utils

import android.view.MenuItem
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken

object MapTypeConverter {

    @TypeConverter
    @JvmStatic
    fun stringToMap(value: JsonElement): Map<String, String> {
        return Gson().fromJson(value,  object : TypeToken<Map<MenuItem, Int>>() {}.type)
    }

    @TypeConverter
    @JvmStatic
    fun mapToString(value: Map<MenuItem, Int>?): String {
        return if(value == null) "" else Gson().toJson(value)
    }
}