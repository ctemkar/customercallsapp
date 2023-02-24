package com.smartshehar.customercallingv2.repositories.sqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.OrderItem
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderDao
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemDao
import com.smartshehar.customercallingv2.utils.MapTypeConverter
import lombok.Synchronized
import androidx.sqlite.db.SupportSQLiteDatabase


@Database(
    entities = [Customer::class, MenuItem::class, CustomerOrder::class, OrderItem::class],
    version = 25
)
@TypeConverters(MapTypeConverter::class)
abstract class AppLocalDatabase : RoomDatabase() {

    abstract fun customerDao(): CustomerDao
    abstract fun menuItemDao(): MenuItemDao
    abstract fun customerOrderDao(): CustomerOrderDao


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