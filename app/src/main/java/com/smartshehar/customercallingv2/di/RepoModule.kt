package com.smartshehar.customercallingv2.di

import android.app.Application
import com.smartshehar.customercallingv2.repositories.CustomerRepository
import com.smartshehar.customercallingv2.repositories.sqlite.AppLocalDatabase
import com.smartshehar.customercallingv2.repositories.sqlite.dao.CustomerDao
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
}