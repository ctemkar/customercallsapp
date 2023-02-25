package com.smartshehar.customercallingv2.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import lombok.*
import java.io.Serializable

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
class Customer : Serializable {

    @PrimaryKey(autoGenerate = true)
    var customerId: Long = 0

    var _id: String = ""

    @ColumnInfo(name = "first_name")
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
    var restaurantId : String = ""
    var isBackupUp = false

}