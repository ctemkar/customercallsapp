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
    fun insert(customer: MenuItem)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(menuItem: MenuItem)

    @Query("select * from menuitem")
    fun findAllMenuItems() : LiveData<List<MenuItem>>

    @Query("select * from menuitem where isBackedUp=0")
    fun findAllNonBackedUpMenuItems() : List<MenuItem>


}