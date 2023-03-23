package com.smartshehar.customercallingv2.models.dtos

class GetOrderItemsRs {

    val orderItems = ArrayList<OrderItemRs>()

    data class OrderItemRs(
        val _id: String,
        val parentOrderId: String,
        val quantity: Int,
        val itemName: String,
        val price: Double,
        val menuItemId: String,
        val createdAt: String
    )

}