package com.smartshehar.customercallingv2.activities.customer.order.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderRepository
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemRepository
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddCustomerOrderVM @Inject constructor(
    private val customerOrderRepository: CustomerOrderRepository,
    private val menuItemRepository: MenuItemRepository,
    application: Application
) : AndroidViewModel(application) {

    private val addCustomerOrderLiveData = MutableLiveData<EventData<CustomerOrder>>()
    fun addCustomerOrder(customerOrder: CustomerOrder): LiveData<EventData<CustomerOrder>> {
        customerOrder.orderId = UUID.randomUUID().toString()
        customerOrder.orderDate = System.currentTimeMillis()
        viewModelScope.launch {
            val eventData = EventData<CustomerOrder>()
            eventData.eventStatus = EventStatus.SUCCESS
            eventData.data = customerOrderRepository.saveCustomerOrder(customerOrder)
            addCustomerOrderLiveData.value = eventData
        }
        return addCustomerOrderLiveData
    }


}