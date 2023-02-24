package com.smartshehar.customercallingv2.repositories.menuitem

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.amaze.emanage.events.EventData
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton.OnChangedCallback
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.dtos.CreateMenuItemRq
import com.smartshehar.customercallingv2.repositories.api.MenuItemApi


class MenuItemRepository(private val menuItemDao: MenuItemDao, val menuItemApi: MenuItemApi) {

    private val TAG = "MenuItemRepository"

    suspend fun saveMenuItem(menuItem: MenuItem): EventData<MenuItem> {
        var eventData = EventData<MenuItem>()
        try {
            //First insert into local db with
            menuItemDao.insert(menuItem)
            eventData = saveMenuItemToApi(menuItem)
            return eventData

        } catch (e: java.lang.Exception) {
            eventData.eventStatus = EventStatus.CACHE_DATA
            return eventData
        }
    }

    private suspend fun saveMenuItemToApi(menuItem: MenuItem): EventData<MenuItem> {
        val eventData = EventData<MenuItem>()
        val createMenuItemRq = getCreateMenuRq(menuItem)
        val response = menuItemApi.createMenuItem(createMenuItemRq)
        if (response.isSuccessful) {
            //Need to modify if only successful
            menuItem.isBackedUp = true
            menuItem.createdAt = response.body()!!.data!!.createdAt
            //If save to api is successful, then save to local db with a true flag
            menuItemDao.update(menuItem)
            eventData.eventStatus = EventStatus.SUCCESS
            return eventData
        }
        eventData.eventStatus = EventStatus.SUCCESS
        return eventData
    }

    private fun getCreateMenuRq(menuItem: MenuItem): CreateMenuItemRq {
        val createMenuItemRq = CreateMenuItemRq()
        createMenuItemRq.itemName = menuItem.itemName
        createMenuItemRq.description = menuItem.description
        createMenuItemRq.price = menuItem.price
        return createMenuItemRq
    }

    fun updateMenuItem(menuItem: MenuItem): EventData<MenuItem> {
        menuItemDao.update(menuItem)
        val eventData = EventData<MenuItem>()
        eventData.eventStatus = EventStatus.SUCCESS
        return eventData
    }

    suspend fun checkAndSyncBackup() {
        val nonBackedUpItems = menuItemDao.findAllNonBackedUpMenuItems()
        Log.d(TAG, "checkAndSyncBackup: ${nonBackedUpItems.size} items not synced")
        for(item in nonBackedUpItems){

        }
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