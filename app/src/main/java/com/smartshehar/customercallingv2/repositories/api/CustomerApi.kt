package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.Customer
import com.smartshehar.customercallingv2.models.dtos.CreateCustomerRq
import com.smartshehar.customercallingv2.models.dtos.SuccessRs
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface CustomerApi {

    @POST("customers")
    suspend fun createCustomer(@Body createCustomerRq: CreateCustomerRq) : Response<SuccessRs<Customer>>
    @GET("customers")
    suspend fun getCustomers(): Response<SuccessRs<List<Customer>>>
}