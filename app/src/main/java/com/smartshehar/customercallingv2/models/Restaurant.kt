package com.smartshehar.customercallingv2.models

import lombok.ToString

@ToString
class Restaurant {
    var restaurantName : String = ""
    var ownerId : String= ""
    var addressLine1 : String = ""
    var addressLine2 : String = ""
    var pincode :Long = 0
}