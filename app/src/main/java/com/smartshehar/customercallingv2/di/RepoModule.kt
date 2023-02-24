package com.smartshehar.customercallingv2.di

import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.api.CustomerApi
import com.smartshehar.customercallingv2.repositories.api.MenuItemApi
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
    fun getCustomerRepo(customerDao: CustomerDao,customerApi: CustomerApi) : CustomerRepository {
        return CustomerRepository(customerDao, customerApi )
    }

    @Provides
    fun getMenuItemRepo(menuItemDao: MenuItemDao,menuItemApi: MenuItemApi) : MenuItemRepository {
        return MenuItemRepository(menuItemDao, menuItemApi)
    }

}