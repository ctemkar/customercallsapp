package com.smartshehar.customercallingv2.activities.customer.order.vieworderdetails

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderRepository
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithOrderItem
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


    private val orderDetailsLiveData = MutableLiveData<EventData<List<OrderItem>>>()

    fun getOrderDetails(orderId: Long): LiveData<EventData<List<OrderItem>>> {
        viewModelScope.launch(Dispatchers.IO) {
            val eventData = EventData<List<OrderItem>>()
            eventData.eventStatus = EventStatus.SUCCESS
            eventData.data = customerOrderRepository.getOrderDetailsV2(orderId)
            orderDetailsLiveData.postValue(eventData)
        }
        return orderDetailsLiveData
    }

    fun destroyObservers() {

    }


}