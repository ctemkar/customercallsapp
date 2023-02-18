package com.smartshehar.customercallingv2.activities.home

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeActivityVM @Inject constructor(
    application: Application,
    private val customerDao: CustomerDao
) : AndroidViewModel(application) {

    private val customersLiveData = MutableLiveData<EventData<List<Customer>>>()


    private val customerListObserver = Observer<List<Customer>> { customer ->
        val data = EventData<List<Customer>>()
        data.data = customer
        data.eventStatus = EventStatus.SUCCESS
        customersLiveData.value = data
    }

    fun getCustomersLiveData(): LiveData<EventData<List<Customer>>> {
        //If observer is not present, then only add new observer
        if(!customerDao.getAllCustomers().hasObservers()) {
            customerDao.getAllCustomers().observeForever(customerListObserver)
        }
        return customersLiveData
    }



    //Must remove observer manually as this one is not lifecycle aware
    override fun onCleared() {
        customerDao.getAllCustomers().removeObserver(customerListObserver)
        super.onCleared()
    }
}