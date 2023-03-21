package com.smartshehar.customercallingv2.repositories.customerorder

import android.app.Application
import android.content.Context.MODE_PRIVATE
import android.util.Log
import com.amaze.emanage.events.EventData
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerOrderRq
import com.smartshehar.customercallingv2.models.dtos.GetCustomerOrderRs
import com.smartshehar.customercallingv2.repositories.api.CustomerOrderApi
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithCustomer
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_KEY_CUSTOMER_ORDERS
import com.smartshehar.customercallingv2.utils.Constants.Companion.PREF_ORDERS_STORE
import com.smartshehar.customercallingv2.utils.RequestHelper
import com.smartshehar.customercallingv2.utils.events.EventStatus
import org.json.JSONObject
import java.lang.reflect.Type
import java.util.*
import javax.inject.Inject
import kotlin.streams.toList

/**
 * Repository layer for customer orders
 * Manages customer orders and responsible for data supply from sqlite db and api
 * Constructors will be injected by dagger hilt
 * Created by Rithik S (coderithik@gmail.com)  on 12/01/2023
 */
class CustomerOrderRepository @Inject constructor(
    private val application: Application,
    private val customerOrderDao: CustomerOrderDao,
    private val customerOrderApi: CustomerOrderApi
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
        orderItems: List<OrderItem>,
        customer: Customer
    ): CustomerOrder {
        val totalAmount = 0.0
        val filteredOrderItems = orderItems.filter {
            it.quantity != 0
        }
        customerOrder.orderTotalAmount = totalAmount

        val savedParentOrderId = customerOrderDao.insert(customerOrder)

        filteredOrderItems.forEach {
            //Map the parent order to child items ie. Cart items
            it.parentOrderId = savedParentOrderId
        }
        customerOrderDao.insertOrderItems(filteredOrderItems)
        customerOrder.orderId = savedParentOrderId
        //Proceed to push into api
        if (customer._id.isNotEmpty()) {
            saveCustomerOrderToApi(
                createCustomerOrderDto(
                    customer._id,
                    filteredOrderItems,
                    savedParentOrderId,
                    customer.customerId
                )
            )
        }
        return customerOrder
    }


    private suspend fun saveCustomerOrderToApi(createCustomerOrderRq: CreateCustomerOrderRq) {
        val gson = Gson()
        val json = gson.toJson(createCustomerOrderRq)
        Log.d(TAG, "saveCustomerOrderToApi: $json")
        val result = customerOrderApi.saveCustomerOrder(createCustomerOrderRq)
        if (result.isSuccessful) {
            Log.d(TAG, "saveCustomerOrderToApi: ${result.body()!!.status}")
        } else {
            Log.d(TAG, "saveCustomerOrderToApi: ERROR ${RequestHelper.getErrorMessage(result)}")
        }

    }

    private fun createCustomerOrderDto(
        customerId: String,
        filteredOrderItems: List<OrderItem>,
        savedParentOrderId: Long,
        localCustomerId: Long
    ): CreateCustomerOrderRq {
        val createCustomerOrderRq = CreateCustomerOrderRq()
        createCustomerOrderRq.customerId = customerId
        createCustomerOrderRq.orderItems = filteredOrderItems as ArrayList<OrderItem>
        createCustomerOrderRq.localOrderId = savedParentOrderId
        createCustomerOrderRq.localCustomerId = localCustomerId
        return createCustomerOrderRq
    }

    suspend fun getCustomerOrders(customerId: Long): CustomerWithCustomerOrder {
        return customerOrderDao.getCustomerOrders(customerId)
    }


    suspend fun fetchCustomerOrdersApiData(apiCustomerId: String): EventStatus {
        //Proceed to fetch API data
        val response = customerOrderApi.getCustomerOrders(apiCustomerId)
        if (response.isSuccessful) {
            val fetchedList = response.body()!!.data
            if (fetchedList != null) {
                if (fetchedList.isNotEmpty()) {
                    customerOrderDao.deleteAllSyncedCustomerOrders(fetchedList[0].customerId)
                    val customerOrdersConverted: List<CustomerOrder> =
                        getOrderItemsFromResponse(fetchedList as ArrayList<GetCustomerOrderRs>)
                    customerOrdersConverted.forEach { it.isBackedUp = true; it.orderId = 0 }
                    customerOrderDao.insertCustomerOrders(customerOrdersConverted)
                    return EventStatus.SUCCESS
                }
            }
        }
        return EventStatus.ERROR
    }

    private fun getOrderItemsFromResponse(fetchedList: ArrayList<GetCustomerOrderRs>): List<CustomerOrder> {
        val responseCustomerOrderList: List<CustomerOrder> =
            fetchedList.stream().map { CustomerOrder.fromCustomerOrderResponse(it) }.toList()
        Log.d(TAG, "getOrderItemsFromResponse: ${responseCustomerOrderList.size}")
        return responseCustomerOrderList
    }


    suspend fun checkAndSyncBackup() {

    }

    suspend fun getAllCustomersOrders(): List<CustomerOrderWithCustomer> {
        return customerOrderDao.getAllCustomerOrders()
    }

    suspend fun getItemOrderedCountByCustomerAndItems(
        menuItemsId: List<OrderItem>,
        customerId: Long
    ): List<OrderItem> {
        //Items will be assigned total times ordered value
        menuItemsId.forEach {
            it.totalOrders =
                customerOrderDao.getOrderItemCountFromOrder(
                    it.localMenuId,
                    customerId
                )
        }
        return menuItemsId
    }

    suspend fun getOrderDetailsV2(parentOrderId: Long): List<OrderItem>? {
        val result = customerOrderDao.getOrderItems(parentOrderId)
        if (result.isEmpty()) {
            return ArrayList()
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