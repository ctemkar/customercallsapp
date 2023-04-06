package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.MenuItem
import com.smartshehar.customercallingv2.models.dtos.CreateMenuItemRq
import com.smartshehar.customercallingv2.models.dtos.SuccessRs
import com.smartshehar.customercallingv2.models.dtos.UpdateMenuItemRq
import com.smartshehar.customercallingv2.models.dtos.UpdateRs
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface MenuItemApi {
    @POST("v1/menuitems")
    suspend fun createMenuItem(@Body createMenuItemRq: CreateMenuItemRq): Response<SuccessRs<MenuItem>>

    @GET("v1/menuitems")
    suspend fun getMenuItems(): Response<SuccessRs<List<MenuItem>>>

    @PATCH("v1/menuitems")
    suspend fun updateMenuItem(@Body updateMenuItemRq: UpdateMenuItemRq): Response<UpdateRs>

}