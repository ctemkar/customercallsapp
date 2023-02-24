package com.smartshehar.customercallingv2.activities.menuitems.add

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.amaze.emanage.events.EventData
import com.amaze.emanage.events.SingleLiveEvent
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.repositories.menuitem.MenuItemRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMenuItemVM @Inject constructor(
    application: Application,
    private val menuItemRepository: MenuItemRepository
) : AndroidViewModel(application) {

    private val addMenuItemLiveData = SingleLiveEvent<EventData<MenuItem>>()

    fun addMenuItem(menuItem: MenuItem) : SingleLiveEvent<EventData<MenuItem>>{
        viewModelScope.launch {
            val result = menuItemRepository.saveMenuItem(menuItem)
            addMenuItemLiveData.value = result
        }
        return addMenuItemLiveData
    }



    fun updateMenuItem(menuItem: MenuItem) : SingleLiveEvent<EventData<MenuItem>>{
        viewModelScope.launch {
            val result = menuItemRepository.updateMenuItem(menuItem)
            addMenuItemLiveData.value = result
        }
        return addMenuItemLiveData
    }



}