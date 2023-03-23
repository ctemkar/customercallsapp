package com.smartshehar.customercallingv2.activities.customer.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.customer.CustomerRepository
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderRepository
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewCustomerVM @Inject constructor(
    application: Application,
    private val customerRepository: CustomerRepository,
    private val customerOrderRepository: CustomerOrderRepository
) : AndroidViewModel(application) {

    private val customerLiveData = MutableLiveData<EventData<Customer>>()

    fun getCustomerData(customerId: Long): LiveData<EventData<Customer>> {
        val eventData = EventData<Customer>()
        eventData.eventStatus = EventStatus.SUCCESS
        eventData.data = customerRepository.getCustomerDetailsById(customerId)
        customerLiveData.value = eventData
        return customerLiveData
    }


    /**
     * Methods and observers to get customer orders placed with customer ID
     * The received data is observed and translated to another livedata to provide to activity
     */
    private val customerOrdersLiveData =
        MutableLiveData<EventData<CustomerWithCustomerOrder>>()

    private val customerOrdersObserver = Observer<CustomerWithCustomerOrder> {
        val eventData = EventData<CustomerWithCustomerOrder>()
        if(it.customerOrders.isEmpty()){
            eventData.eventStatus = EventStatus.EMPTY
        }else{
            eventData.data = it
            eventData.eventStatus = EventStatus.SUCCESS
        }
        customerOrdersLiveData.postValue(eventData)
    }

    fun getCustomerOrders(customerId: Long): LiveData<EventData<CustomerWithCustomerOrder>> {
        viewModelScope.launch {
            val eventData = EventData<CustomerWithCustomerOrder>()
            eventData.eventStatus = EventStatus.SUCCESS

            customerOrderRepository.getCustomerOrders(customerId)
                .observeForever(customerOrdersObserver)

            //Proceed to sync with server
            val customerDetails = customerRepository.getCustomerDetailsById(customerId)
            if (customerDetails._id.isNotBlank()) {
                //Only if the customer has been synced, then only we will be having the server db id
                //with that ID we will be saving in the servers, so checking only if the ID is present in local also
                customerOrderRepository.fetchCustomerOrdersFromServer(
                    customerDetails._id,
                    customerId
                )
            }

        }
        return customerOrdersLiveData
    }



}