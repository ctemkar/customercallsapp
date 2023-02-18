package com.smartshehar.customercallingv2.repositories.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.sqlite.reations.CustomerWithCustomerOrder

@Dao
interface CustomerDao {

    @Insert
    fun insert(customer: Customer)

    @Update
    fun update(customer: Customer)

    @Query("select * from customer where customerId=:customerId")
    fun getCustomerById(customerId: Long) : Customer

    @Query("select * from customer")
    fun getAllCustomers(): LiveData<List<Customer>>

    @Query("select * from customer where contactNumber=:phoneNumber")
    fun getCustomerByPhoneNumber(phoneNumber: String): Customer



}