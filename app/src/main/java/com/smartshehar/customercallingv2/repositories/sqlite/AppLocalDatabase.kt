package com.smartshehar.customercallingv2.repositories.sqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemDao
import lombok.Synchronized

@Database(entities = [Customer::class, MenuItem::class], version = 3)
abstract class AppLocalDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun menuItemDao(): MenuItemDao

    companion object {
        private var instance: AppLocalDatabase? = null

        @Synchronized
        fun getInstance(context: Context): AppLocalDatabase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppLocalDatabase::class.java,
                    "APP_DATABASE"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build()
            }
            return instance!!
        }

    }


}