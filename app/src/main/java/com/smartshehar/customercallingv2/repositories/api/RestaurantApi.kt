package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.Restaurant
import com.smartshehar.customercallingv2.models.dtos.CreateRestaurantRq
import com.smartshehar.customercallingv2.models.dtos.SuccessRs
import com.smartshehar.customercallingv2.models.dtos.UpdateRs
import com.smartshehar.customercallingv2.models.dtos.UpdateSelectedRestaurantRq
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST

interface RestaurantApi {

    @GET("restaurants")
    suspend fun getRestaurants() : Response<SuccessRs<List<Restaurant>>>

    @POST("restaurants")
    suspend fun addRestaurant(@Body createRestaurantRq: CreateRestaurantRq) : Response<SuccessRs<Restaurant>>

    @PATCH("restaurants/updateselected")
    suspend fun updateSelectedRestaurant(@Body updateRq : UpdateSelectedRestaurantRq) : Response<UpdateRs>

}