package com.smartshehar.customercallingv2.repositories.customerorder

import android.util.Log
import com.amaze.emanage.events.EventData
import com.google.gson.reflect.TypeToken
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerOrderRq
import com.smartshehar.customercallingv2.models.dtos.GetCustomerOrderRs
import com.smartshehar.customercallingv2.models.dtos.GetOrderItemsRs
import com.smartshehar.customercallingv2.repositories.api.CustomerOrderApi
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithCustomer
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder
import com.smartshehar.customercallingv2.utils.Constants.Companion.NETWORK_ERROR
import com.smartshehar.customercallingv2.utils.events.EventStatus
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
    private val customerOrderDao: CustomerOrderDao,
    private val customerOrderApi: CustomerOrderApi
) {

    private val TAG = "CustomerOrderRepository"

    suspend fun saveCustomerOrderToLocalAndApi(
        customerOrder: CustomerOrder,
        orderItems: List<OrderItem>,
        customer: Customer
    ): CustomerOrder {

        val filteredOrderItems = orderItems.filter {
            it.quantity != 0
        }
        val savedParentOrderId = saveCustomerOrderToLocal(customerOrder, filteredOrderItems)
        //Proceed to push into api
        if (customer._id.isNotEmpty()) {
            //If ID of customer in server is present, then only save the data
            //Or else we can't save the data with local generated ID
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

    private fun saveCustomerOrderToLocal(
        customerOrder: CustomerOrder,
        filteredOrderItems: List<OrderItem>
    ): Long {
        val totalAmount = 0.0
        customerOrder.orderTotalAmount = totalAmount
        val savedParentOrderId = customerOrderDao.insert(customerOrder)

        filteredOrderItems.forEach {
            //Map the parent order to child items ie. Cart items
            it.parentOrderId = savedParentOrderId
        }
        customerOrderDao.insertOrderItems(filteredOrderItems)
        customerOrder.orderId = savedParentOrderId
        return savedParentOrderId
    }


    private suspend fun saveCustomerOrderToApi(createCustomerOrderRq: CreateCustomerOrderRq) {
        val result = customerOrderApi.saveCustomerOrder(createCustomerOrderRq)
        if (result.isSuccessful) {

        } else {

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


    suspend fun fetchCustomerOrdersApiData(
        apiCustomerId: String,
        localCustomerId: Long
    ): EventStatus {
        //Proceed to fetch API data
        val response = customerOrderApi.getCustomerOrders(apiCustomerId)
        if (response.isSuccessful) {
            val fetchedList = response.body()!!.data
            if (fetchedList != null) {
                if (fetchedList.isNotEmpty()) {
                    customerOrderDao.deleteAllSyncedCustomerOrders(localCustomerId)
                    val customerOrdersConverted: List<CustomerOrder> =
                        getCustomerOrdersFromResponse(fetchedList as ArrayList<GetCustomerOrderRs>)
                    customerOrdersConverted.forEach { it.isBackedUp = true; }
                    customerOrderDao.insertCustomerOrders(customerOrdersConverted)
                    return EventStatus.SUCCESS
                }
            }
        }
        return EventStatus.ERROR
    }

    private fun getCustomerOrdersFromResponse(fetchedList: ArrayList<GetCustomerOrderRs>): List<CustomerOrder> {
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

    /**
     * Function to get the number of times that the customer ordered a specific item
     */
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

    suspend fun getOrderItemsInCustomerOrder(parentOrderId: Long): List<OrderItem> {
        val result = customerOrderDao.getOrderItems(parentOrderId)
        if (result.isEmpty()) {
            return ArrayList()
        }
        return result
    }


    //Helper functions
    private fun getCustomerOrderListType(): Type {
        return object : TypeToken<LinkedList<CustomerOrder>>() {}.type
    }

    suspend fun getOrderItemsInCustomerOrderFromServer(
        serverCustomerId: String,
        orderId: String
    ): EventData<List<OrderItem>> {
        val eventData = EventData<List<OrderItem>>()
        try {
            val response =
                customerOrderApi.getOrderItemsFromCustomerOrder(serverCustomerId, orderId)
            if (response.isSuccessful) {
                eventData.eventStatus = EventStatus.SUCCESS
                if (response.body() != null) {
                    val responseOrderItems : GetOrderItemsRs = response.body()!!.data!!
                    eventData.data = OrderItem.fromOrderItemListResponse(responseOrderItems.orderItems)
                    eventData.eventStatus = EventStatus.SUCCESS
                    return eventData
                }
            }
        } catch (e: java.lang.Exception) {
            eventData.eventStatus = EventStatus.ERROR
            eventData.error = NETWORK_ERROR
        }
        eventData.eventStatus = EventStatus.ERROR
        eventData.error = "Unable to fetch data"
        return eventData
    }


}