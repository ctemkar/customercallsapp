package com.smartshehar.customercallingv2.di

import android.app.Application
import com.smartshehar.customercallingv2.repositories.sqlite.AppLocalDatabase
import com.smartshehar.customercallingv2.repositories.sqlite.dao.CustomerDao
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

}