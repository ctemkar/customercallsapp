package com.smartshehar.customercallingv2.activities.customer.order.vieworderdetails

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderRepository
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewOrderDetailsVM @Inject constructor(
    application: Application,
    private val customerOrderRepository: CustomerOrderRepository
) : AndroidViewModel(application) {


    private val orderDetailsLiveData = MutableLiveData<EventData<CustomerOrder>>()

    fun getOrderDetails(orderId: String, customerId: Long): LiveData<EventData<CustomerOrder>> {
        viewModelScope.launch(Dispatchers.IO) {
            val eventData = EventData<CustomerOrder>()
            eventData.eventStatus = EventStatus.SUCCESS
            eventData.data = customerOrderRepository.getOrderDetails(orderId,customerId).data
            orderDetailsLiveData.value = eventData
        }
        return orderDetailsLiveData
    }

    fun destroyObservers() {

    }


}