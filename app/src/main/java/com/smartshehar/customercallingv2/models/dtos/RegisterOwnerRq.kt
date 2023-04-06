package com.smartshehar.customercallingv2.models.dtos

data class RegisterOwnerRq(
    var ownerName: String? = null,
    var contactNumber : Long,
    var password: String
)
