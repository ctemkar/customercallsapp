package com.smartshehar.customercallingv2.activities.menuitems.view

import android.app.Application
import androidx.lifecycle.*
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ViewMenuItemVM @Inject constructor(
    application: Application,
    private val menuItemRepository: MenuItemRepository
) : AndroidViewModel(application) {


    private val menuItemsLiveData = MutableLiveData<EventData<List<MenuItem>>>()
    private val menuItemsObserver = Observer<EventData<List<MenuItem>>> {
        val eventData = EventData<List<MenuItem>>()
        eventData.data = eventData.data
        eventData.eventStatus = eventData.eventStatus
        menuItemsLiveData.value = eventData
    }

    fun getMenuItems(): LiveData<EventData<List<MenuItem>>> {
        return menuItemRepository.getAllMenuItems()
    }

    fun checkPendingBackups() {
        viewModelScope.launch {
            menuItemRepository.checkAndSyncBackup()
        }
    }


    override fun onCleared() {
        super.onCleared()
        if (menuItemRepository.getAllMenuItems().hasActiveObservers()) {
            menuItemRepository.getAllMenuItems().removeObserver(menuItemsObserver)
        }
    }


}