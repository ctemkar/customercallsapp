package com.smartshehar.customercallingv2.activities.customer.addcustomer

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.api.CustomerApi
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import com.smartshehar.customercallingv2.repositories.customer.CustomerRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddCustomerVM @Inject constructor(
    application: Application,
    private val customerRepository: CustomerRepository
) : AndroidViewModel(application) {


    private val createCustomerStatusLiveData = SingleLiveEvent<EventData<Customer>>()
    private val TAG = "NewCustomerVM"

    fun createNewCustomer(customer: Customer): SingleLiveEvent<EventData<Customer>> {
        viewModelScope.launch {
            val result = customerRepository.createCustomer(customer)
            createCustomerStatusLiveData.postValue(result)
        }
        return createCustomerStatusLiveData
    }

}