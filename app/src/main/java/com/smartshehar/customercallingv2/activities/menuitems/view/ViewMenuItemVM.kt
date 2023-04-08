package com.smartshehar.customercallingv2.activities.menuitems.view

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemRepository
import com.smartshehar.customercallingv2.utils.events.EventStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewMenuItemVM @Inject constructor(
    application: Application,
    private val menuItemRepository: MenuItemRepository
) : AndroidViewModel(application) {


    private val menuItemsLiveData = MutableLiveData<EventData<List<MenuItem>>>()

    fun menuItemsLiveData(): LiveData<EventData<List<MenuItem>>> {
        return menuItemsLiveData
    }

    fun refreshMenuItems (){
        notifyDataChange()
        viewModelScope.launch {
            //Fetch local data first and sent to view
            //Once local data is loaded, then proceed to fetch api data
            menuItemRepository.getMenuItemsFromApi()
            notifyDataChange()
        }
    }

    private fun notifyDataChange() {
        val cachedItems = menuItemRepository.getAllMenuItems()
        val eventData = EventData<List<MenuItem>>()
        eventData.eventStatus = EventStatus.CACHE_DATA
        eventData.data = cachedItems
        menuItemsLiveData.postValue(eventData)
    }

    fun checkPendingBackups() {
        viewModelScope.launch {
            menuItemRepository.checkAndSyncBackup()
        }
    }


}