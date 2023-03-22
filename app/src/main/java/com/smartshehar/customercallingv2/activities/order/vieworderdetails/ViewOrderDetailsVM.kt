package com.smartshehar.customercallingv2.activities.order.vieworderdetails

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.OrderItem
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


    private val orderDetailsLiveData = MutableLiveData<EventData<List<OrderItem>>>()

    fun getOrderItemsInCustomerOrder(
        orderId: Long,
        customerId: Long
    ): LiveData<EventData<List<OrderItem>>> {
        viewModelScope.launch(Dispatchers.IO) {
            val eventData = EventData<List<OrderItem>>()
            eventData.eventStatus = EventStatus.SUCCESS

            var orderItems = customerOrderRepository.getOrderItemsInCustomerOrder(orderId)
            if (orderItems.isNotEmpty()) {
                orderItems = customerOrderRepository.getItemOrderedCountByCustomerAndItems(
                    orderItems,
                    customerId
                )
            }
            eventData.data = orderItems
            orderDetailsLiveData.postValue(eventData)
        }
        return orderDetailsLiveData
    }


    private val orderItemsFromServerLiveData = MutableLiveData<EventData<List<OrderItem>>>()
    fun getOrderItemsInCustomerOrderFromServer(
        serverCustomerId: String,
        serverOrderId: String
    ): LiveData<EventData<List<OrderItem>>> {
        viewModelScope.launch(Dispatchers.IO) {
            val fetchOrderEvent =
                customerOrderRepository.getOrderItemsInCustomerOrderFromServer(
                    serverCustomerId,
                    serverOrderId
                )
            orderItemsFromServerLiveData.postValue(fetchOrderEvent)
        }
        return orderItemsFromServerLiveData
    }


    override fun onCleared() {
        super.onCleared()
    }


}