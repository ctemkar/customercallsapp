package com.smartshehar.customercallingv2.activities.customer.order.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderRepository
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemRepository
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerOrderVM @Inject constructor(
    private val customerOrderRepository: CustomerOrderRepository,
    private val menuItemRepository: MenuItemRepository,
    application: Application
) : AndroidViewModel(application) {

    private val addCustomerOrderLiveData = MutableLiveData<EventData<CustomerOrder>>()
    fun addCustomerOrder(
        customerId: Long,
        orderItems: ArrayList<OrderItem>
    ): LiveData<EventData<CustomerOrder>> {
        val customerOrder = CustomerOrder()
        //customerOrder.orderId = UUID.randomUUID().toString()
        customerOrder.customerId = customerId
        customerOrder.orderDate = System.currentTimeMillis()
        var totalAmount = 0.0
        for (order in orderItems) {
            if (order.quantity == 0) {
                orderItems.remove(order)
                break
            }
            totalAmount += order.quantity * order.price
        }
        customerOrder.orderTotalAmount = totalAmount

        viewModelScope.launch {
            val eventData = EventData<CustomerOrder>()
            eventData.eventStatus = EventStatus.SUCCESS
            eventData.data = customerOrderRepository.saveCustomerOrderV2(customerOrder, orderItems)
            addCustomerOrderLiveData.value = eventData
        }
        return addCustomerOrderLiveData
    }

    suspend fun getOrderedCounts(orderItems : List<OrderItem>, customerId: Long) : List<OrderItem>{
        return customerOrderRepository.getItemOrderedCountByCustomerAndItems(
            orderItems,
            customerId
        )

    }



}