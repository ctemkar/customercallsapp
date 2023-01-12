package com.smartshehar.customercallingv2.repositories.customerorder

import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.utils.events.EventStatus


class CustomerOrderRepository(private val customerOrderDao: CustomerOrderDao) {

    suspend fun saveCustomerOrder(customerOrder: CustomerOrder): CustomerOrder {
        val eventData = EventData<CustomerOrder>()
        eventData.eventStatus = EventStatus.SUCCESS
        eventData.data = CustomerOrder()
        return customerOrderDao.insert(customerOrder)
    }

}