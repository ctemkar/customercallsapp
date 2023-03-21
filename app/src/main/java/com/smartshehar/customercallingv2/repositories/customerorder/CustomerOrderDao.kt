package com.smartshehar.customercallingv2.repositories.customerorder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerOrderWithCustomer
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder

@Dao
interface CustomerOrderDao {

    @Insert
    fun insert(customerOrder: CustomerOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomerOrders(customerOrders: List<CustomerOrder>): List<Long>


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

    @Query("select count (price) from orderitem,customerorder where menuItemId=:itemId and customerId=:customerId and parentOrderId=orderId")
    fun getOrderItemCountFromOrder(itemId: Long, customerId: Long): Int

    @Transaction
    @Query("select * from customerorder")
    fun getAllCustomerOrders(): List<CustomerOrderWithCustomer>

    @Query("delete from customerorder where customerId=:customerId and isBackedUp=1")
    fun deleteAllSyncedCustomerOrders(customerId: Long)

}