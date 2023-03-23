package com.smartshehar.customercallingv2.repositories.sqlite.reations

import androidx.room.Embedded
import androidx.room.Relation
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import lombok.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class CustomerOrderWithOrderItem {
    @Embedded
    lateinit var customerOrder: CustomerOrder
    @Relation(
        parentColumn = "orderId",
        entityColumn = "parentOrderId",
        entity = OrderItem::class
    )
    lateinit var customerOrders: List<OrderItem>
}