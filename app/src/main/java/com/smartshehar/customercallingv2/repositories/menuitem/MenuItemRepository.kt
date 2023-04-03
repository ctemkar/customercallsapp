package com.smartshehar.customercallingv2.repositories.menuitem

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.amaze.emanage.events.EventData
import com.smartshehar.customercallingv2.utils.events.EventStatus
import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.dtos.CreateMenuItemRq
import com.smartshehar.customercallingv2.models.dtos.UpdateMenuItemRq
import com.smartshehar.customercallingv2.repositories.api.MenuItemApi
import com.smartshehar.customercallingv2.utils.Constants.Companion.NETWORK_ERROR

/**
 * Repository layer for menu items
 * Manages customer orders and responsible for data supply from sqlite db and api
 * Constructors will be injected by dagger hilt
 * Created by Rithik S (coderithik@gmail.com)  on 12/02/2023
 */
class MenuItemRepository(
    private val menuItemDao: MenuItemDao,
    private val menuItemApi: MenuItemApi,
    private val restaurantId: String
) {


    private val TAG = "MenuItemRepository"


    suspend fun saveMenuItem(menuItem: MenuItem): EventData<MenuItem> {
        var eventData = EventData<MenuItem>()
        try {
            //First insert into local db with
            val insertedId = menuItemDao.insert(menuItem)
            menuItem.itemId = insertedId
            eventData = saveMenuItemToApi(menuItem)
            return eventData

        } catch (e: java.lang.Exception) {
            eventData.eventStatus = EventStatus.CACHE_DATA
            return eventData
        }
    }




    suspend fun updateMenuItem(menuItem: MenuItem): EventData<MenuItem> {
        var eventData: EventData<MenuItem>
        eventData = updateMenuItemToApi(menuItem)
        return eventData
    }

    suspend fun checkAndSyncBackup() {
        val nonBackedUpItems = menuItemDao.findAllUnSyncedMenuItems(restaurantId)
        Log.d(TAG, "checkAndSyncBackup: ${nonBackedUpItems.size} items not synced")
        for (item in nonBackedUpItems) {
            val event = saveMenuItemToApi(item)
            if (event.eventStatus == EventStatus.SUCCESS) {
                item.isBackedUp = true
                updateMenuItem(item)
            }
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
        eventData.eventStatus = EventStatus.ERROR
        return eventData
    }

    private suspend fun updateMenuItemToApi(menuItem: MenuItem): EventData<MenuItem> {
        val eventData = EventData<MenuItem>()
        val updateMenuItemRq = getUpdateMenuRq(menuItem)
        val response = menuItemApi.updateMenuItem(updateMenuItemRq)
        if (response.isSuccessful) {
            //Need to modify if only successful
            menuItem.isBackedUp = true
            //If save to api is successful, then save to local db with a true flag
            menuItemDao.update(menuItem)
            eventData.eventStatus = EventStatus.SUCCESS
            return eventData
        }
        menuItemDao.update(menuItem)
        eventData.eventStatus = EventStatus.ERROR
        return eventData
    }

    suspend fun getMenuItemsFromApi(): EventData<List<MenuItem>> {
        val eventData = EventData<List<MenuItem>>()
        try {
            val response = menuItemApi.getMenuItems()
            if (response.isSuccessful) {
                if (response.body() != null) {
                    if (response.body()!!.data != null) {
                        eventData.eventStatus = EventStatus.SUCCESS
                        eventData.data = response.body()!!.data
                        //Proceed to sync with local sqlite database
                        syncDataWithCacheData(eventData.data)
                        return eventData
                    }
                }
            }
        } catch (e: java.lang.Exception) {
            eventData.eventStatus = EventStatus.ERROR
            eventData.error = NETWORK_ERROR
            return eventData

        }
        eventData.eventStatus = EventStatus.ERROR
        eventData.error = "Unable to fetch"
        return eventData
    }

    private fun syncDataWithCacheData(fetchedItems: List<MenuItem>?) {
        menuItemDao.deleteSyncedMenuItems(restaurantId)
        if (fetchedItems != null) {
            for (item in fetchedItems) {
                item.isBackedUp = true
            }
            menuItemDao.insert(fetchedItems)
        }
    }


    private val menuItemsLiveData = MutableLiveData<EventData<List<MenuItem>>>()
    private val menuItemsLocalObserver = Observer<List<MenuItem>> {
        //Observer will receive the data when there is a change in local sqlite
        //database, this will pass the data to livedata listeners
        val eventData = EventData<List<MenuItem>>()
        eventData.eventStatus = EventStatus.SUCCESS
        eventData.data = it
        menuItemsLiveData.postValue(eventData)
    }


    fun getAllMenuItems(): LiveData<EventData<List<MenuItem>>> {
        menuItemDao.findAllMenuItems(restaurantId).observeForever(menuItemsLocalObserver)
        return menuItemsLiveData
    }

    private fun getCreateMenuRq(menuItem: MenuItem): CreateMenuItemRq {
        val createMenuItemRq = CreateMenuItemRq()
        createMenuItemRq.itemName = menuItem.itemName
        createMenuItemRq.description = menuItem.description
        createMenuItemRq.price = menuItem.price
        createMenuItemRq.category = menuItem.category
        createMenuItemRq.restaurantId = restaurantId
        return createMenuItemRq
    }

    private fun getUpdateMenuRq(menuItem: MenuItem): UpdateMenuItemRq {
        val updateMenuItemRq = UpdateMenuItemRq()
        updateMenuItemRq._id = menuItem._id
        updateMenuItemRq.itemName = menuItem.itemName
        updateMenuItemRq.description = menuItem.description
        updateMenuItemRq.price = menuItem.price
        updateMenuItemRq.category = menuItem.category
        return updateMenuItemRq
    }


}