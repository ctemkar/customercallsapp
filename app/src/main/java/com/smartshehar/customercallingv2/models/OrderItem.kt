package com.smartshehar.customercallingv2.models

import android.view.Menu
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import lombok.AllArgsConstructor
import lombok.Getter
import lombok.NoArgsConstructor
import lombok.Setter

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity(
    foreignKeys = [ForeignKey(
        entity = CustomerOrder::class,
        parentColumns = arrayOf("orderId"),
        childColumns = arrayOf("parentOrderId"),
        onDelete = CASCADE
    )]
)
class OrderItem {

    @PrimaryKey(autoGenerate = true)
    var orderItemId: Long = 0
    var parentOrderId: Long = 0
    var quantity: Int = 0
    var itemName: String = ""
    var price: Double = 0.0
    var category: String = ""
    var menuItemId: Long = 0
    var totalOrders = 0


}