package com.smartshehar.customercallingv2.repositories.customer

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerRq
import com.smartshehar.customercallingv2.repositories.api.CustomerApi
import com.smartshehar.customercallingv2.utils.RequestHelper
import com.smartshehar.customercallingv2.utils.events.EventStatus
import kotlinx.coroutines.*
import javax.inject.Inject

/**
 * Respository layer for customers
 * Manages customer caching and responsible for data supply from sqlite db and api
 * Constructors will be injected by dagger hilt
 * Created by Rithik S (coderithik@gmail.com) on 10/01/2023
 */
class CustomerRepository @Inject constructor(
    private val customerDao: CustomerDao,
    private var customerApi: CustomerApi
) {

    private val TAG = "CustomerRepository"


    fun getCustomerDetailsById(customerId: Long): Customer {
        return customerDao.getCustomerById(customerId)
    }

    suspend fun createCustomer(customer: Customer): EventData<Customer> =
        withContext(Dispatchers.IO) {
            Log.d(
                TAG,
                "createCustomer: 1 ${customer.addressLine1} ${customer.firstName} ${customer.pincode} ${customer.contactNumber}"
            )
            val eventData = EventData<Customer>()
            val insertedID = customerDao.insert(customer)
            customer.customerId = insertedID
            val createCustomerRq = CreateCustomerRq(customer)
            val result = customerApi.createCustomer(createCustomerRq)
            if (result.isSuccessful) {
                //Save in local also
                Log.d(TAG, "createCustomer: ${result.code()} ${result.body()}")
                eventData.eventStatus = EventStatus.SUCCESS
            } else {
                eventData.eventStatus = EventStatus.ERROR
                Log.d(TAG, "createCustomer: ${RequestHelper.getErrorMessage(result)}")
            }
            return@withContext eventData
        }


    fun getCustomers(): LiveData<List<Customer>> {
        return customerDao.getAllCustomers()
    }

    suspend fun fetchApiData() : EventStatus{
        //Proceed to fetch API data
        val result = customerApi.getCustomers()
        if (result.isSuccessful) {
            val fetchedList = result.body()?.data
            if (fetchedList != null) {
                if (fetchedList.isEmpty()) {
                    customerDao.deleteAllCustomerDetails()
                } else {
                    customerDao.deleteCustomersWithRestaurantId(fetchedList[0].restaurantId)
                    customerDao.insertCustomers(fetchedList)
                    return EventStatus.SUCCESS
                }
            }
        }
        return EventStatus.ERROR
    }

    fun compareAndCacheData(cachedList: List<Customer>, fetchedList: List<Customer>) {

    }

}