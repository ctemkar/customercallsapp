package com.smartshehar.customercallingv2.models

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.smartshehar.customercallingv2.models.dtos.GetCustomerOrderRs
import lombok.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity(
    foreignKeys = [ForeignKey(
        entity = Customer::class,
        parentColumns = arrayOf("customerId"),
        childColumns = arrayOf("customerId")
    )]
)
class CustomerOrder {
    @PrimaryKey(autoGenerate = true)
    var orderId: Long = 0
    var customerId: Long = 0

    var orderTotalAmount: Double = 0.0
    var orderDate: Long = 0
    var isBackedUp: Boolean = false

    var serverCustomerId: String = ""

    companion object {
        fun fromCustomerOrderResponse(customerOrderRs: GetCustomerOrderRs): CustomerOrder {
            val customerOrder = CustomerOrder()
            customerOrder.serverCustomerId = customerOrderRs.customerId
            customerOrder.customerId = customerOrderRs.localCustomerId
            customerOrder.orderTotalAmount = customerOrderRs.orderTotalAmount
            customerOrder.orderId = customerOrderRs.localOrderId
            return customerOrder
        }
    }

}