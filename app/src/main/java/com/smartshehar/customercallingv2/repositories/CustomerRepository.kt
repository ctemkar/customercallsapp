package com.smartshehar.customercallingv2.repositories

import android.util.Log
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.sqlite.dao.CustomerDao

class CustomerRepository(val customerDao: CustomerDao) {
    private val TAG = "CustomerRepository"


    fun getCustomerDetailsWithNumber(unformattedContactNumber: String): Customer {
        var contactNumber = unformattedContactNumber
        if (contactNumber.startsWith("+91")) {
            contactNumber = unformattedContactNumber.substring(3)
        }
        Log.d(TAG, "getCustomerDetailsWithNumber: $contactNumber")
        return customerDao.getCustomerWithPhoneNumber(contactNumber)
    }
}