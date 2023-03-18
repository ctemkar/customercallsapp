package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerOrderRq
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerOrderRs
import com.smartshehar.customercallingv2.models.dtos.SuccessRs
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface CustomerOrderApi {

    @Headers("Accept: application/json")
    @POST("/restaurants/orders")
    suspend fun saveCustomerOrder(@Body customerOrderRq: CreateCustomerOrderRq) : Response<SuccessRs<CreateCustomerOrderRs>>

}