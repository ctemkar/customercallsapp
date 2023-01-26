package com.smartshehar.customercallingv2.repositories.menuitem

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.MenuItem


class MenuItemRepository(private val menuItemDao: MenuItemDao) {


    fun saveMenuItem(menuItem: MenuItem): EventData<MenuItem> {
        menuItemDao.insert(menuItem)
        val eventData = EventData<MenuItem>()
        eventData.eventStatus = EventStatus.SUCCESS
        return eventData
    }

    fun updateMenuItem(menuItem: MenuItem) : EventData<MenuItem>{
        menuItemDao.update(menuItem)
        val eventData = EventData<MenuItem>()
        eventData.eventStatus = EventStatus.SUCCESS
        return eventData
    }


    private val menuItemsLiveData = MutableLiveData<EventData<List<MenuItem>>>()
    private val menuItemsLocalObserver = Observer<List<MenuItem>> {
        //Observer will receive the data when there is a change in local sqlite
        //database, this will pass the data to livedata listeners
        val eventData = EventData<List<MenuItem>>()
        eventData.eventStatus = EventStatus.SUCCESS
        eventData.data = it
        menuItemsLiveData.value = eventData
    }


    fun getAllMenuItems(): LiveData<EventData<List<MenuItem>>> {
        menuItemDao.findAllMenuItems().observeForever(menuItemsLocalObserver)
        return menuItemsLiveData
    }

}