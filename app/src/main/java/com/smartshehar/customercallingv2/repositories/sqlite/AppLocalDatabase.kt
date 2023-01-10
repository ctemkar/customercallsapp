package com.smartshehar.customercallingv2.repositories.sqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.sqlite.dao.CustomerDao
import lombok.Synchronized

@Database(entities = [Customer::class], version = 2)
abstract class AppLocalDatabase : RoomDatabase() {

    abstract fun customerDao() : CustomerDao

    companion object{
        private var instance : AppLocalDatabase? = null

        @Synchronized
        fun getInstance(context: Context) : AppLocalDatabase {
            if(instance == null){
                 instance = Room.databaseBuilder(context.applicationContext, AppLocalDatabase::class.java, "APP_DATABASE")
                     .fallbackToDestructiveMigration()
                     .allowMainThreadQueries()
                     .build()
            }
            return instance!!
        }


    }


}