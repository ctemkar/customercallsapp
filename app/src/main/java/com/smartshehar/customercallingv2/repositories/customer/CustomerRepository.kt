package com.smartshehar.customercallingv2.repositories.customer

import android.util.Log
import com.smartshehar.customercallingv2.models.Customer

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