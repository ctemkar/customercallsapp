package com.smartshehar.customercallingv2.activities.order.viewallorders

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderRepository
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithCustomer
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllOrdersVM @Inject constructor(
    application: Application,
    private val customerOrderRepository: CustomerOrderRepository
) : AndroidViewModel(application) {


    private val allOrdersLiveData = MutableLiveData<EventData<List<CustomerOrderWithCustomer>>>()

     fun getAllCustomerOrders() : LiveData<EventData<List<CustomerOrderWithCustomer>>> {
        viewModelScope.launch {
            val eventData = EventData<List<CustomerOrderWithCustomer>>()
            eventData.eventStatus = EventStatus.SUCCESS
            eventData.data = customerOrderRepository.getAllCustomersOrders()
            allOrdersLiveData.postValue(eventData)
        }
        return allOrdersLiveData
    }


}