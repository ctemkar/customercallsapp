package com.smartshehar.customercallingv2.di

import android.app.Application
import com.smartshehar.customercallingv2.repositories.sqlite.AppLocalDatabase
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import com.smartshehar.customercallingv2.repositories.customerorder.CustomerOrderDao
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object DBModule {

    @Provides
    fun getCustomerDao(application: Application) : CustomerDao {
        return AppLocalDatabase.getInstance(application).customerDao()
    }

    @Provides
    fun getMenuItemDao(application: Application) : MenuItemDao {
        return AppLocalDatabase.getInstance(application).menuItemDao()
    }

    @Provides
    fun getCustomerOrderDao(application: Application) : CustomerOrderDao {
        return AppLocalDatabase.getInstance(application).customerOrderDao()
    }
}