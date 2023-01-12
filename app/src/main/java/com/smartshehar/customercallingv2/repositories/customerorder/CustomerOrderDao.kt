package com.smartshehar.customercallingv2.repositories.customerorder

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.smartshehar.customercallingv2.models.CustomerOrder

@Dao
interface CustomerOrderDao {

    @Insert
    fun insert(customerOrder: CustomerOrder) : CustomerOrder


    @Query("select * from customerorder where customerId=:customerId")
    fun getOrderByCustomerId(customerId: Long)

}