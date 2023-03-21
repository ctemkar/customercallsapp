package com.smartshehar.customercallingv2.models.dtos

import com.smartshehar.customercallingv2.models.CustomerOrder
import lombok.ToString

@ToString
data class CreateCustomerOrderRs(
    val customerId: String = "",
    val localCustomerId: Long = 0,
    val localOrderId: Long = 0,
    val orderItems: ArrayList<CustomerOrder>
)