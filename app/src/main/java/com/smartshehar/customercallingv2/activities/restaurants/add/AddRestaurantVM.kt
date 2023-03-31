package com.smartshehar.customercallingv2.activities.restaurants.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.Restaurant
import com.smartshehar.customercallingv2.models.dtos.CreateRestaurantRq
import com.smartshehar.customercallingv2.repositories.api.RestaurantApi
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddRestaurantVM @Inject constructor(
    application: Application,
    private val restaurantApi: RestaurantApi
) : AndroidViewModel(application) {

    private val createRestaurantLiveData = MutableLiveData<EventData<Restaurant>>()

    fun createRestaurant(createRestaurantRq: CreateRestaurantRq) {
        viewModelScope.launch {
            val eventData = EventData<Restaurant>()
            try {
                val response = restaurantApi.addRestaurant(createRestaurantRq)
                if (response.isSuccessful) {
                    eventData.eventStatus = EventStatus.SUCCESS
                } else {
                    eventData.eventStatus = EventStatus.ERROR
                    eventData.error = "Unable to save to server"
                }
            } catch (e: Exception) {
                eventData.eventStatus = EventStatus.ERROR
                eventData.error = "Network error, Please check your network"
            }
            createRestaurantLiveData.postValue(eventData)
        }
    }

    fun getCreateRestaurantLiveData(): LiveData<EventData<Restaurant>> {
        return createRestaurantLiveData
    }


}