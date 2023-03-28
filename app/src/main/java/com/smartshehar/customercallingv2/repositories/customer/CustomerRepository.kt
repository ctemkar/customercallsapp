package com.smartshehar.customercallingv2.repositories.customer

import android.util.Log
import androidx.lifecycle.LiveData
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerRq
import com.smartshehar.customercallingv2.repositories.api.CustomerApi
import com.smartshehar.customercallingv2.utils.Constants.Companion.NETWORK_ERROR
import com.smartshehar.customercallingv2.utils.events.EventStatus
import kotlinx.coroutines.*
import java.util.*
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
            val createdId = customerDao.insert(customer)
            customer.customerId = createdId
            return@withContext saveCustomerToApi(customer)
        }

    private suspend fun saveCustomerToApi(customer: Customer): EventData<Customer> {
        val eventData = EventData<Customer>()
        try {
            val createCustomerRq = CreateCustomerRq(customer)
            val result = customerApi.createCustomer(createCustomerRq)
            if (result.isSuccessful) {
                //Save in local also
                customer.isBackupUp = true
                customerDao.update(customer)
                eventData.eventStatus = EventStatus.SUCCESS
                return eventData
            }
        } catch (e: Exception) {
            eventData.eventStatus = EventStatus.ERROR
            eventData.error = NETWORK_ERROR
        }
        eventData.eventStatus = EventStatus.ERROR
        eventData.error = "Unable to save"
        return eventData
    }


    fun getCustomers(): LiveData<List<Customer>> {
        return customerDao.getAllCustomers()
    }

    suspend fun fetchApiData(): EventStatus {
        //Proceed to fetch API data
        val response = customerApi.getCustomers()
        if (response.isSuccessful) {
            val fetchedList = response.body()!!.data
            if (fetchedList != null) {
                if (fetchedList.isEmpty()) {
                    customerDao.deleteAllCustomerDetails()
                } else {
                    customerDao.deleteAllSyncedCustomers()
                    fetchedList.forEach { it.isBackupUp = true }
                    customerDao.insertCustomers(fetchedList)
                }
                return EventStatus.SUCCESS
            }
        }
        return EventStatus.ERROR
    }

    suspend fun checkAndSyncBackup() {
        val customers = customerDao.getAllUnSyncedCustomers()
        for (customer in customers) {
            val event = saveCustomerToApi(customer)
            if (event.eventStatus == EventStatus.SUCCESS) {
                customer.isBackupUp = true
                customerDao.update(customer)
            }
        }
    }


    fun compareAndCacheData(cachedList: List<Customer>, fetchedList: List<Customer>) {

    }

}