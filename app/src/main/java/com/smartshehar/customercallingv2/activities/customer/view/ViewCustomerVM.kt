package com.smartshehar.customercallingv2.activities.customer.view

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
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

    private val customerOrdersLiveData =
        MutableLiveData<EventData<CustomerWithCustomerOrder>>()

    fun getCustomerOrders(customerId: Long): LiveData<EventData<CustomerWithCustomerOrder>> {
        viewModelScope.launch {
            val eventData = EventData<CustomerWithCustomerOrder>()
            eventData.eventStatus = EventStatus.SUCCESS
            eventData.data = customerOrderRepository.getCustomerOrders(customerId)
            customerOrdersLiveData.postValue(eventData)

            //Proceed to sync with server
            val customerDetails = customerRepository.getCustomerDetailsById(customerId)
            if(customerDetails._id.isNotBlank()){
                //Only if the customer has been synced, then only we will be having the server db id
                //with that ID we will be saving in the servers, so checking only if the ID is present in local also
                customerOrderRepository.fetchCustomerOrdersApiData(customerDetails._id,customerId)
            }

        }
        return customerOrdersLiveData
    }



}