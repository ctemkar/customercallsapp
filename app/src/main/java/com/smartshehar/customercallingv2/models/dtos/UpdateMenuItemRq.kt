package com.smartshehar.customercallingv2.models.dtos

class UpdateMenuItemRq {
    lateinit var _id : String
    var itemName : String = ""
    var price : Double = 0.0
    var description : String = ""
    var category : String = ""
}