package com.smartshehar.customercallingv2.repositories.customerorder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithOrderItem
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder

@Dao
interface CustomerOrderDao {

    @Insert
    fun insert(customerOrder: CustomerOrder): Long

    @Insert
    fun insertOrderItems(orderItems: List<OrderItem>): List<Long>

    @Query("select * from customerorder where customerId=:customerId")
    fun getOrderByCustomerId(customerId: Long): CustomerOrder

    @Transaction
    @Query("select * from customer where customerId=:customerId")
    fun getCustomerOrders(customerId: Long): CustomerWithCustomerOrder

    @Transaction
    @Query("select * from orderitem where parentOrderId=:parentOrderId")
    fun getOrderItems(parentOrderId: Long): List<OrderItem>

}