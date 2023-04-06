package com.smartshehar.customercallingv2.models.dtos

data class CreateRestaurantRq(
    var restaurantName: String,
    var contactNumber: String,
    var addressLine1 : String,
    var addressLine2 : String,
    var pincode: Long
)
//restaurantName, contactNumber, addressLine1, addressLine2, pincode

