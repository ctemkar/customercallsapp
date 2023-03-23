package com.smartshehar.customercallingv2.models.dtos

import com.smartshehar.customercallingv2.models.OrderItem

class CreateCustomerOrderRq {
    var localCustomerId: Long = 0
    var localOrderId : Long= 0
    var customerId : String = ""
    lateinit var orderItems : ArrayList<OrderItem>
}