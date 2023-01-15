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
    var getMiCustomerLocalId = 0
    var miCustomerCallId = 0
    var msFirstName = ""
    var msLastName = ""
    var msCompany = ""
    var msCountryCode = ""
    var msNationalNumber = ""

    var msPhoneNo = ""
    var msFlatNo = ""
    var msWing = ""
    var msFloorNo = ""
    var msBulding = ""
    var msArea = ""
    var msRoad = ""
    var msLandmark1 = ""
    var msLandmark2 = ""
    var msComplex: String? = null
    var msCity: String? = null
    var msState: String? = null
    var msCountry: String? = null
    var msPostalCode: String? = null
    var msClientDateTime: String? = null
    var msCustomerInfoAll: String? = null
    var mIsIgnore = false
    var mIsDeleted = false
    var mIsNew = true
    var dateCreated: Long = -1

}