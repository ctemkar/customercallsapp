package com.smartshehar.customercallingv2.repositories.customerorder

import android.app.Application
import android.content.Context.MODE_PRIVATE
import com.google.gson.Gson
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_KEY_CUSTOMER_ORDERS
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_ORDERS_STORE
import javax.inject.Inject
import java.util.ArrayList

import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type


class CustomerOrderRepository @Inject constructor(val application: Application) {

    suspend fun saveCustomerOrder(customerOrder: CustomerOrder): CustomerOrder {
        val prefs = application.getSharedPreferences(PREF_ORDERS_STORE, MODE_PRIVATE)
        val editor = prefs.edit()
        var savedList = HashSet<CustomerOrder>()


        val gson = Gson()
        //If orders are already present, then load the list into savedList variable
        if (prefs.contains(PREF_KEY_CUSTOMER_ORDERS)) {
            val type: Type = object : TypeToken<ArrayList<CustomerOrder?>?>() {}.type
            savedList = gson.fromJson(prefs.getString(PREF_KEY_CUSTOMER_ORDERS, ""), type)
        }

        savedList.add(customerOrder)
        editor.putString(PREF_KEY_CUSTOMER_ORDERS, gson.toJson(savedList))
        editor.apply()
        return CustomerOrder()
    }

}