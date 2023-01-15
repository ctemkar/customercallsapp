package com.smartshehar.customercallingv2.repositories.customerorder

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amaze.emanage.events.EventData
import com.google.gson.Gson
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_KEY_CUSTOMER_ORDERS
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_ORDERS_STORE
import javax.inject.Inject

import com.google.gson.reflect.TypeToken
import com.smartshehar.customercallingv2.utils.events.EventStatus
import java.lang.reflect.Type
import java.util.*


class CustomerOrderRepository @Inject constructor(val application: Application) {

    private val TAG = "CustomerOrderRepository"
    suspend fun saveCustomerOrder(customerOrder: CustomerOrder): CustomerOrder {
        val prefs = application.getSharedPreferences(PREF_ORDERS_STORE, MODE_PRIVATE)
        val editor = prefs.edit()
        var savedList = LinkedList<CustomerOrder>()


        val prefKey =
            PREF_KEY_CUSTOMER_ORDERS + customerOrder.customerId //Making the key unique for all customers

        Log.d(TAG, "saveCustomerOrder: $prefKey")
        //If orders are already present, then load the list into savedList variable
        val gson = Gson()
        if (prefs.contains(prefKey)) {
            savedList = gson.fromJson(prefs.getString(prefKey, ""), getCustomerOrderListType())
        }
        savedList.add(customerOrder)
        editor.putString(prefKey, gson.toJson(savedList))
        editor.apply()
        return CustomerOrder()
    }

    suspend fun getCustomerOrders(customerId: Long): LinkedList<CustomerOrder> {
        val prefs = application.getSharedPreferences(PREF_ORDERS_STORE, MODE_PRIVATE)
        var savedList = LinkedList<CustomerOrder>()
        val gson = Gson()
        val prefKey =
            PREF_KEY_CUSTOMER_ORDERS + customerId //Making the key unique for all customers

        Log.d(TAG, "getCustomerOrders: $prefKey")
        //If orders are already present, then load the list into savedList variable
        if (prefs.contains(prefKey)) {
            val json = prefs.getString(prefKey, "")
            if (!json.isNullOrBlank()) {
                savedList = gson.fromJson(json, getCustomerOrderListType())
            }
        } else {
            Log.d(TAG, "getCustomerOrders: Not con")
        }
        return savedList
    }


    suspend fun getOrderDetails(
        orderId: String,
        customerId: Long
    ): EventData<CustomerOrder> {


        val prefs = application.getSharedPreferences(PREF_ORDERS_STORE, MODE_PRIVATE)
        //Initially get data from shared preferences
        val prefKey =
            PREF_KEY_CUSTOMER_ORDERS + customerId //Making the key unique for all customers

        val eventData = EventData<CustomerOrder>()
        Log.d(TAG, "getCustomerOrders: $prefKey")
        //If orders are already present, then load the list into savedList variable
        if (!prefs.contains(prefKey)) {
            //replace with getting data from api and then return error message
            eventData.eventStatus = EventStatus.EMPTY
            eventData.error = "No Order Found"
        }


        val json = prefs.getString(prefKey, "")
        val gson = Gson()
        if (!json.isNullOrBlank()) {
            val savedList: LinkedList<CustomerOrder> =
                gson.fromJson(json, getCustomerOrderListType())
            val customerOrder = getCustomerOrderFromList(savedList, orderId)
            if (Objects.isNull(customerOrder)) {
                eventData.data = customerOrder
                Log.d(TAG, "getOrderDetails: found")
                eventData.eventStatus = EventStatus.SUCCESS
            } else {
                Log.d(TAG, "getOrderDetails: Not found")
                eventData.error = "Order is not found"
                eventData.eventStatus = EventStatus.ERROR
            }
        }


        return eventData
    }


    /**
     * Checks if an order is present in the list
     *
     * @param savedList List of customer orders
     * @param orderId Id of the order to check
     */
    private fun getCustomerOrderFromList(
        savedList: LinkedList<CustomerOrder>,
        orderId: String
    ): CustomerOrder? {
        var customerOrder = CustomerOrder()
        customerOrder.orderId = orderId
        //order id is only enough to compare as equals method has been overridden in CustomerOrder class
        if (!savedList.contains(customerOrder)) {
            return null
        }

        val index = savedList.indexOf(customerOrder)
        customerOrder = savedList[index]
        return customerOrder
    }

    //Helper functions
    private fun getCustomerOrderListType(): Type {
        return object : TypeToken<LinkedList<CustomerOrder>>() {}.type
    }


}