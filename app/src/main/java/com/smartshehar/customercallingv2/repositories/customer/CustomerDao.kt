package com.smartshehar.customercallingv2.repositories.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.smartshehar.customercallingv2.models.Customer

@Dao
interface CustomerDao {

    @Insert
    fun insert(customer : Customer)

    @Update
    fun update(customer : Customer)

    @Query("select * from customer")
    fun getAllCustomers(): LiveData<List<Customer>>

    @Query("select * from customer where msPhoneNo=:phoneNumber")
    fun getCustomerWithPhoneNumber(phoneNumber: String) : Customer

}