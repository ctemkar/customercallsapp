package com.smartshehar.customercallingv2.models

import android.view.Menu
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.ForeignKey.CASCADE
import androidx.room.PrimaryKey
import com.smartshehar.customercallingv2.models.dtos.GetOrderItemsRs
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
    var menuItemId: String = ""
    var localMenuId: Long = 0
    var totalOrders = 0
    var isBackedUp: Boolean = false
    var serverOrderItemId: String = ""
    var serverParentOrderId : String = ""


    companion object {
        fun fromOrderItemListResponse(orderListResponse: List<GetOrderItemsRs.OrderItemRs>): ArrayList<OrderItem> {
            val orderItemsList = ArrayList<OrderItem>()
            orderListResponse.forEach {
                val orderItem = OrderItem()
                orderItem.apply {
                    serverOrderItemId = it._id
                    serverParentOrderId = it.parentOrderId
                    quantity = it.quantity
                    itemName = it.itemName
                    price= it.price
                    menuItemId = it.menuItemId
                }
                orderItemsList.add(orderItem)
            }
            return orderItemsList
        }
    }

}