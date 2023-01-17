package com.smartshehar.customercallingv2.models

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
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
    var orderId: Int = 0
    var customerId: Long = 0
    var orderTotal: Double = 0.0
    var orderDate: Long = 0

}