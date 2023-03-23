package com.smartshehar.customercallingv2.models.dtos

data class GetCustomerOrderRs(
    var _id: String,
    var customerId: String = "",
    var localCustomerId: Long = 0,
    var localOrderId: Long = 0,
    var orderTotalAmount: Double = 0.0,
    var restaurantId: String = "",
    var createdAt: String,
    var orderId: Long
)
