package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.dtos.CreateMenuItemRq
import com.smartshehar.customercallingv2.models.dtos.SuccessRs
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface MenuItemApi {

    @POST("v1/menuitems")
    suspend fun createMenuItem(@Body createMenuItemRq: CreateMenuItemRq) : Response<SuccessRs<MenuItem>>

}