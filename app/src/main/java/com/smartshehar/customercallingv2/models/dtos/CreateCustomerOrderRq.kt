package com.smartshehar.customercallingv2.models.dtos

import com.smartshehar.customercallingv2.models.OrderItem

class CreateCustomerOrderRq {
    var orderId : Long= 0
    var customerId : String = ""
    lateinit var orderItems : ArrayList<OrderItem>
}