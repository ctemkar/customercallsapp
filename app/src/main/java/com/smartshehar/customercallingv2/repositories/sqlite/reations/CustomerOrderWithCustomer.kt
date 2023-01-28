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
class CustomerOrderWithCustomer {

    @Embedded
    lateinit var customerOrder: CustomerOrder


    @Relation(
        parentColumn = "customerId",
        entityColumn = "customerId",
        entity = Customer::class
    )
    lateinit var customer: Customer
}
