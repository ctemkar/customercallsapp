package com.smartshehar.customercallingv2.activities.auth

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.models.Owner
import com.smartshehar.customercallingv2.models.dtos.LoginRq
import com.smartshehar.customercallingv2.models.dtos.LoginRs
import com.smartshehar.customercallingv2.repositories.api.AuthApi
import com.smartshehar.customercallingv2.utils.Constants.Companion.NETWORK_ERROR
import com.smartshehar.customercallingv2.utils.RequestHelper
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.utils.states.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import javax.inject.Inject

/**
 * ViewModel for Authentication and Profile Activities
 */
@HiltViewModel
class AuthenticationVM @Inject constructor(
    application: Application,
    private val authApi: AuthApi,
    private val authState: AuthState
) : AndroidViewModel(application) {

    private val TAG = "AuthenticationVM"
    private var loginLiveData: SingleLiveEvent<EventData<LoginRs>>? = null

    fun loginOwner(loginRq: LoginRq): SingleLiveEvent<EventData<LoginRs>> {
        if (loginLiveData == null) {
            loginLiveData = SingleLiveEvent()
        }
        viewModelScope.launch {
            val eventData = EventData<LoginRs>()
            try {
                val result = authApi.loginOwner(loginRq)
                if (result.isSuccessful) {
                    eventData.data = result.body()
                    eventData.eventStatus = EventStatus.SUCCESS
                    if (result.body() != null) {
                        authState.saveCurrentUserToken(result.body()!!.token)
                    }
                } else {
                    eventData.error = RequestHelper.getErrorMessage(result)
                    eventData.eventStatus = EventStatus.ERROR
                }
            } catch (e: Exception) {
                eventData.error = "Connection Error!"
                eventData.eventStatus = EventStatus.ERROR
            }

            loginLiveData!!.postValue(eventData)
        }

        return loginLiveData as SingleLiveEvent<EventData<LoginRs>>
    }


    /*
    Get the profile of the owner, by passing the JWT token saved in sharedpreferences
     */
    private var profileLiveData: SingleLiveEvent<EventData<Owner>>? = null

    fun getProfileData(): SingleLiveEvent<EventData<Owner>> {
        if (profileLiveData == null) {
            profileLiveData = SingleLiveEvent()
        }
        viewModelScope.launch {
            val eventData = EventData<Owner>()
            try {
                val result = authApi.getOwnerProfile()
                if (result.isSuccessful) {
                    eventData.data = result.body()!!.data
                    eventData.eventStatus = EventStatus.SUCCESS
                } else {
                    eventData.error = RequestHelper.getErrorMessage(result)
                    eventData.eventStatus = EventStatus.ERROR
                }
            } catch (e: Exception) {
                eventData.eventStatus = EventStatus.ERROR
                eventData.error = "NETWORK"
            }
            profileLiveData!!.postValue(eventData)
        }

        return profileLiveData as SingleLiveEvent<EventData<Owner>>
    }

}