package com.smartshehar.customercallingv2.di

import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.customer.CustomerRepository
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemDao
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepoModule {

    @Provides
    fun getCustomerRepo(customerDao: CustomerDao) : CustomerRepository {
        return CustomerRepository(customerDao)
    }

    @Provides
    fun getMenuItemRepo(menuItemDao: MenuItemDao) : MenuItemRepository {
        return MenuItemRepository(menuItemDao)
    }

}