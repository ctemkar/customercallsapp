package com.smartshehar.customercallingv2.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import lombok.*
import java.io.Serializable
import java.util.*

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
class Customer : Serializable{

    @PrimaryKey(autoGenerate = true)
    var customerId: Int = 0

    @ColumnInfo(name = "first_name")
    var firstName: String? = null
    var msLastName = ""
    var msCountryCode = ""
    var msNationalNumber = ""
    var msPhoneNo = ""
    var houseNo = ""
    var totalOrders = 0
    var area : String = ""
    var landmark : String = ""
    var mIsIgnore = false
    var mIsDeleted = false
    var mIsNew = true
    var dateCreated: Long = -1

}