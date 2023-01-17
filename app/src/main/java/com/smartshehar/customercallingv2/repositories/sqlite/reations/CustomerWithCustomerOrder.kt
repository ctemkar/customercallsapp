package com.smartshehar.customercallingv2.repositories.sqlite.reations

import androidx.room.Embedded
import androidx.room.Relation
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import lombok.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class CustomerWithCustomerOrder {

    @Embedded
    lateinit var customer: Customer

    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId",
        entity = CustomerOrder::class
    )
    lateinit var customerOrders: List<CustomerOrder>
}
