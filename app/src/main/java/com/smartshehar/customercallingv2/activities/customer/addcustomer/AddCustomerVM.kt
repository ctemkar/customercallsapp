package com.smartshehar.customercallingv2.activities.customer.addcustomer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AddCustomerVM @Inject constructor(
    application: Application,
    private val customerDao: CustomerDao
) : AndroidViewModel(application) {


    private val createCustomerStatusLiveData = SingleLiveEvent<EventData<String>>()
    private val TAG = "NewCustomerVM"

    fun createNewCustomer(customer: Customer): SingleLiveEvent<EventData<String>> {
        customerDao.insert(customer)
        val data = EventData<String>()
        data.eventStatus = EventStatus.SUCCESS
        createCustomerStatusLiveData.value = data
        return createCustomerStatusLiveData
    }

}