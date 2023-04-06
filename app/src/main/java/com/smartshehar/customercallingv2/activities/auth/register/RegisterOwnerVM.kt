package com.smartshehar.customercallingv2.activities.auth.register

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.models.Owner
import com.smartshehar.customercallingv2.models.dtos.RegisterOwnerRq
import com.smartshehar.customercallingv2.repositories.api.AuthApi
import com.smartshehar.customercallingv2.utils.ResponseHelper
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch

import javax.inject.Inject

@HiltViewModel
class RegisterOwnerVM @Inject constructor(
    application: Application,
    private val authApi: AuthApi
) : AndroidViewModel(application) {

    private val registerOwnerStatusLiveData = SingleLiveEvent<EventData<Owner>>()

    fun registerOwner(createOwnerRq: RegisterOwnerRq) {
        viewModelScope.launch {
            val response = authApi.registerOwner(createOwnerRq)
            val eventData = EventData<Owner>()
            if (response.isSuccessful) {
                eventData.eventStatus = EventStatus.SUCCESS
                eventData.data = response.body()!!.data
            } else {
                eventData.eventStatus = EventStatus.ERROR
                eventData.error = ResponseHelper.getErrorMessage(response)
            }
            registerOwnerStatusLiveData.postValue(eventData)
        }
    }

    fun getRegisterStatusLiveData(): LiveData<EventData<Owner>> {
        return registerOwnerStatusLiveData
    }

}