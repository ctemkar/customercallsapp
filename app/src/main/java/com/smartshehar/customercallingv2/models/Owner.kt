package com.smartshehar.customercallingv2.models

import lombok.ToString

@ToString
class Owner {
    var ownerName : String= ""
    var ownerId : String = ""
    var contactNumber : Long =0
    var selectedRestaurant : Restaurant? = null
    var createdAt : String = ""

    override fun toString(): String {
        return "$ownerName $ownerId $contactNumber }"
    }
}