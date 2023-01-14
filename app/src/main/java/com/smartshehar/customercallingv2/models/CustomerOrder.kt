package com.smartshehar.customercallingv2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import lombok.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class CustomerOrder {

    var orderId: Int = 0
    var orderTotal: Double = 0.0
    var orderItems: HashMap<MenuItem, Int> = HashMap()
    var orderDate: Long = 0
    lateinit var customerId: Customer

}