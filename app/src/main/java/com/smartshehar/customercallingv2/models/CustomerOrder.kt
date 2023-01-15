package com.smartshehar.customercallingv2.models

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import lombok.*
import java.lang.reflect.Type

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class CustomerOrder {

    var orderId: String = ""
    var orderTotal: Double = 0.0
    private var orderItems: HashMap<String, Int> = HashMap()
    var orderDate: Long = 0
    var customerId: Long = 0


    fun addOrderItem(menuItem: MenuItem, quantity: Int) {
        val gson = Gson()
        orderItems[gson.toJson(menuItem)] = quantity
    }

    fun getOrderItems(): HashMap<MenuItem, Int> {
        val map = HashMap<MenuItem, Int>()
        val gson = Gson()
        val type: Type = object : TypeToken<MenuItem>() {}.type
        orderItems.forEach { (json, quantity) ->
            map[gson.fromJson(json, type)] = quantity
        }
        return map
    }

    override fun equals(other: Any?): Boolean {
        return this.orderId.equals((other as CustomerOrder).customerId)
    }

}