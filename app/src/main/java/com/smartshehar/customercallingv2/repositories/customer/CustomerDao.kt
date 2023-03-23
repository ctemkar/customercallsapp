package com.smartshehar.customercallingv2.repositories.customer

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartshehar.customercallingv2.models.Customer

@Dao
interface CustomerDao {

    @Insert
    fun insert(customer: Customer): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertCustomers(customers: List<Customer>)

    @Update
    fun update(customer: Customer)

    @Query("select * from customer where customerId=:customerId")
    fun getCustomerById(customerId: Long): Customer

    @Query("select * from customer")
    fun getAllCustomers(): LiveData<List<Customer>>

    @Query("delete from customer where restaurantId=:restaurantId")
    fun deleteCustomersWithRestaurantId(restaurantId: String)

    @Query("delete from customer where isBackupUp=1")
    fun deleteAllCustomerDetails()

    @Query("select * from customer where contactNumber=:phoneNumber and isBackupUp=1")
    fun getCustomerByPhoneNumber(phoneNumber: String): Customer

    @Query("select * from customer where isBackupUp=0")
    fun getAllUnSyncedCustomers(): List<Customer>

    @Query("delete from customer where isBackupUp=1")
    fun deleteAllSyncedCustomers()


}