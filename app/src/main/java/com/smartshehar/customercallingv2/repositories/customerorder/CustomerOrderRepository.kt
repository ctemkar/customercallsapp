package com.smartshehar.customercallingv2.repositories.customerorder

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.amaze.emanage.events.EventData
import com.google.gson.Gson
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_KEY_CUSTOMER_ORDERS
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_ORDERS_STORE
import javax.inject.Inject

import com.google.gson.reflect.TypeToken
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithCustomer
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder
import com.smartshehar.customercallingv2.utils.events.EventStatus
import java.lang.reflect.Type
import java.util.*
import kotlin.collections.ArrayList


class CustomerOrderRepository @Inject constructor(
    private val application: Application,
    private val customerOrderDao: CustomerOrderDao,
) {

    private val TAG = "CustomerOrderRepository"

    @Deprecated(message = "Replaced shared preference with sqlite")
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


    suspend fun saveCustomerOrderV2(
        customerOrder: CustomerOrder,
        orderItems: List<OrderItem>
    ): CustomerOrder {
        val id = customerOrderDao.insert(customerOrder)
        orderItems.forEach {
            it.parentOrderId = id
            Log.d(TAG, "saveCustomerOrderV2: ${id}")
        }
        val orderItems1 = customerOrderDao.insertOrderItems(orderItems)
        for (orderItem in orderItems1) {
            Log.d(TAG, "saveCustomerOrderV2: Inserted ID ${orderItem}")
        }
        return customerOrder
    }

    suspend fun getCustomerOrders(customerId: Long): CustomerWithCustomerOrder {
        return customerOrderDao.getCustomerOrders(customerId)
    }

    suspend fun getAllCustomersOrders() : List<CustomerOrderWithCustomer>{
        return customerOrderDao.getAllCustomerOrders()
    }

//    suspend fun getCustomerOrders(customerId: Long): LinkedList<CustomerOrder> {
//        val prefs = application.getSharedPreferences(PREF_ORDERS_STORE, MODE_PRIVATE)
//        var savedList = LinkedList<CustomerOrder>()
//        val gson = Gson()
//        val prefKey =
//            PREF_KEY_CUSTOMER_ORDERS + customerId //Making the key unique for all customers
//
//        Log.d(TAG, "getCustomerOrders: $prefKey")
//        //If orders are already present, then load the list into savedList variable
//        if (prefs.contains(prefKey)) {
//            val json = prefs.getString(prefKey, "")
//            if (!json.isNullOrBlank()) {
//                savedList = gson.fromJson(json, getCustomerOrderListType())
//            }
//        } else {
//            Log.d(TAG, "getCustomerOrders: Not con")
//        }
//        return savedList
//    }


    suspend fun getItemOrderedCountByCustomerAndItems(
        menuItemsId: List<OrderItem>,
        customerId: Long
    ): List<OrderItem> {
        //Items will be assigned total times ordered value
        menuItemsId.forEach {
            it.totalOrders =
                customerOrderDao.getOrderItemCountFromOrder(
                    it.menuItemId,
                    customerId
                )
            Log.d(TAG, "getItemOrderedCountByCustomerAndItems: ${it.totalOrders}")

        }
        return menuItemsId
    }

    suspend fun getOrderDetailsV2(parentOrderId: Long): List<OrderItem>? {
        val result = customerOrderDao.getOrderItems(parentOrderId)
        if (result.isEmpty()) {
            Log.d(TAG, "getOrderDetailsV2: Null value $parentOrderId")
            return ArrayList()
        }
        result.forEach {
            Log.d(
                TAG,
                "getOrderDetailsV2: ${it.orderItemId} Parent ID : ${it.parentOrderId} ${it.itemName} ${it.quantity}"
            )
        }
        return result
    }

    @Deprecated(message = "Replaced shared preference with sqlite")
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
            Log.d(TAG, "getOrderDetails: $json")
            Log.d(TAG, "getOrderDetails: list size ${savedList.size}")
            val customerOrder = getCustomerOrderFromList(savedList, orderId)
            if (null != customerOrder) {
                eventData.data = customerOrder
                Log.d(TAG, "getOrderDetails: found ${eventData.data}")
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

        savedList.forEach {
//            if(it.orderId == orderId){
//                return it
//            }
        }

        return null
    }

    //Helper functions
    private fun getCustomerOrderListType(): Type {
        return object : TypeToken<LinkedList<CustomerOrder>>() {}.type
    }


}