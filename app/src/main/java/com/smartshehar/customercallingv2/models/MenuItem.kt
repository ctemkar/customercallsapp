package com.smartshehar.customercallingv2.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import lombok.*

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
class MenuItem : java.io.Serializable {

    @PrimaryKey(autoGenerate = true)
    var itemId: Long = 0
    var _id : String = "" //Mongo ID
    var createdAt: String = ""
    var itemName: String = ""
    var description: String = ""
    var shortName: String = ""
    var updatedAt: String = ""
    var price: Double = 0.0
    var category: String = ""
    var restaurantId: String = ""
    var isBackedUp = false
}