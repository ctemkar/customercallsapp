package com.smartshehar.customercallingv2.repositories.menuitem

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.smartshehar.customercallingv2.models.MenuItem


@Dao
interface MenuItemDao {

    //Dao for Room Database

    @Insert
    fun insert(menuItem: MenuItem) : Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(menuItems: List<MenuItem>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(menuItem: MenuItem)

    @Query("select * from menuitem where restaurantId=:restaurantId")
    fun findAllMenuItems(restaurantId: String)  : List<MenuItem>

    @Query("select * from menuitem where isBackedUp=0 and restaurantId=:restaurantId")
    fun findAllUnSyncedMenuItems(restaurantId: String) : List<MenuItem>

    @Query("delete from menuitem where isBackedUp=1 and restaurantId=:restaurantId")
    fun deleteSyncedMenuItems(restaurantId: String)

}