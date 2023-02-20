package com.smartshehar.customercallingv2.activities.home

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.Restaurant
import com.smartshehar.customercallingv2.models.dtos.UpdateRs
import com.smartshehar.customercallingv2.models.dtos.UpdateSelectedRestaurantRq
import com.smartshehar.customercallingv2.repositories.api.RestaurantApi
import com.smartshehar.customercallingv2.repositories.customer.CustomerDao
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import kotlinx.coroutines.selects.select
import org.json.JSONObject
import javax.inject.Inject

@HiltViewModel
class HomeActivityVM @Inject constructor(
    application: Application,
    private val customerDao: CustomerDao,
    private val restaurantApi: RestaurantApi
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
        if (!customerDao.getAllCustomers().hasObservers()) {
            customerDao.getAllCustomers().observeForever(customerListObserver)
        }
        return customersLiveData
    }


    private val restaurantsLiveData = SingleLiveEvent<EventData<List<Restaurant>>>()
    fun getRestaurantsList(): LiveData<EventData<List<Restaurant>>> {
        viewModelScope.launch {
            val eventData = EventData<List<Restaurant>>()
            val response = restaurantApi.getRestaurants()
            if (response.isSuccessful && response.body() != null) {
                eventData.eventStatus = EventStatus.SUCCESS
                eventData.data = response.body()!!.data
            } else {
                eventData.eventStatus = EventStatus.ERROR
                eventData.data = response.body()!!.data
            }
            restaurantsLiveData.postValue(eventData)
        }
        return restaurantsLiveData
    }


    private val updateSelectedStatusLiveData = MutableLiveData<EventData<UpdateRs>>()
    fun updateSelectedRestaurant(selectedId: String): LiveData<EventData<UpdateRs>> {
        viewModelScope.launch {
            val updateRq = UpdateSelectedRestaurantRq()
            updateRq.restaurantId = selectedId
            val result = restaurantApi.updateSelectedRestaurant(updateRq)
            val eventData = EventData<UpdateRs>()
            if (result.isSuccessful) {
                eventData.eventStatus = EventStatus.SUCCESS
            }else{
                eventData.eventStatus = EventStatus.ERROR
            }
            updateSelectedStatusLiveData.postValue(eventData)
        }
        return updateSelectedStatusLiveData
    }

    //Must remove observer manually as this one is not lifecycle aware
    override fun onCleared() {
        customerDao.getAllCustomers().removeObserver(customerListObserver)
        super.onCleared()
    }
}