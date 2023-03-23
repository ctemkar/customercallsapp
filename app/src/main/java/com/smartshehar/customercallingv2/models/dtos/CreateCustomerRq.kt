package com.smartshehar.customercallingv2.models.dtos

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.smartshehar.customercallingv2.models.Customer

class CreateCustomerRq(customer: Customer) {

    var customerId: Long = 0
    var firstName: String? = null
    var contactNumber = ""
    var houseNo = ""
    var totalOrders = 0
    var area: String = ""
    var addressLine1: String = ""
    var addressLine2 : String = ""
    var landmark: String = ""
    var pincode : Long = 0
    var mIsIgnore = false
    var mIsDeleted = false
    var mIsNew = true
    var dateCreated: Long = -1

    init {
        customerId = customer.customerId
        firstName = customer.firstName
        contactNumber = customer.contactNumber

        addressLine1 = customer.addressLine1
        addressLine2 = customer.addressLine2
        pincode = customer.pincode
    }
}