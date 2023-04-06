package com.smartshehar.customercallingv2.activities.home

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.Restaurant
import com.smartshehar.customercallingv2.models.dtos.UpdateRs
import com.smartshehar.customercallingv2.models.dtos.UpdateSelectedRestaurantRq
import com.smartshehar.customercallingv2.repositories.api.RestaurantApi
import com.smartshehar.customercallingv2.repositories.customer.CustomerRepository
import com.smartshehar.customercallingv2.utils.Constants.Companion.NETWORK_ERROR
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.Exception

@HiltViewModel
class HomeActivityVM @Inject constructor(
    application: Application,
    private val customerRepository: CustomerRepository,
    private val restaurantApi: RestaurantApi,
) : AndroidViewModel(application) {

    private val customersLiveData = MutableLiveData<EventData<List<Customer>>>()


    private val customerListObserver = Observer<List<Customer>> {
        val data = EventData<List<Customer>>()
        data.eventStatus = EventStatus.CACHE_DATA
        data.data = it
        customersLiveData.postValue(data)
    }

    fun getCustomersLiveData(): LiveData<EventData<List<Customer>>> {
        //If observer is not present, then only add new observer
        viewModelScope.launch {
            if (!customerRepository.getCustomers().hasObservers()) {
                customerRepository.getCustomers().observeForever(customerListObserver)
            }
            syncCustomerListDataFromServer()
        }
        return customersLiveData
    }

    private suspend fun syncCustomerListDataFromServer() {
        val eventData = EventData<List<Customer>>()
        try {
            val fetchStatus = customerRepository.fetchApiData()
            customerRepository.checkAndSyncBackup()
            eventData.eventStatus = fetchStatus
            customersLiveData.postValue(eventData)
        } catch (e: Exception) {
            eventData.eventStatus = EventStatus.ERROR
            eventData.error = NETWORK_ERROR
            customersLiveData.postValue(eventData)
        }
    }

    fun refreshCustomerListData(){
        viewModelScope.launch {
            val eventData = EventData<List<Customer>>()
            eventData.eventStatus = EventStatus.LOADING
            customersLiveData.postValue(eventData)
            syncCustomerListDataFromServer()
        }
    }



    private val restaurantsLiveData = SingleLiveEvent<EventData<List<Restaurant>>>()
    fun getRestaurantsList(): LiveData<EventData<List<Restaurant>>> {
        viewModelScope.launch {
            val eventData = EventData<List<Restaurant>>()
            try {
                val response = restaurantApi.getRestaurants()
                if (response.isSuccessful && response.body() != null) {
                    eventData.eventStatus = EventStatus.SUCCESS
                    eventData.data = response.body()!!.data
                } else {
                    eventData.eventStatus = EventStatus.ERROR
                    eventData.data = response.body()!!.data
                }
                restaurantsLiveData.postValue(eventData)
            } catch (e: java.lang.Exception) {
                eventData.eventStatus = EventStatus.ERROR
            }
        }
        return restaurantsLiveData
    }


    private val updateSelectedStatusLiveData = MutableLiveData<EventData<UpdateRs>>()
    fun getSelectedRestaurantUpdateLiveData(): LiveData<EventData<UpdateRs>> {
        return updateSelectedStatusLiveData
    }

    fun updateCurrentSelectedRestaurant(selectedId: String){
        val eventData = EventData<UpdateRs>()
        eventData.eventStatus = EventStatus.LOADING
        updateSelectedStatusLiveData.postValue(eventData)
        viewModelScope.launch {
            try {
                val updateRq = UpdateSelectedRestaurantRq()
                updateRq.restaurantId = selectedId
                val result = restaurantApi.updateSelectedRestaurant(updateRq)
                if (result.isSuccessful) {
                    eventData.eventStatus = EventStatus.SUCCESS
                } else {
                    eventData.eventStatus = EventStatus.ERROR
                }
            }catch (e: Exception){
                eventData.eventStatus = EventStatus.ERROR
            }
            updateSelectedStatusLiveData.postValue(eventData)
        }
    }

    //Must remove observer manually as this one is not lifecycle aware
    override fun onCleared() {
        customerRepository.getCustomers().removeObserver(customerListObserver)
        super.onCleared()
    }
}