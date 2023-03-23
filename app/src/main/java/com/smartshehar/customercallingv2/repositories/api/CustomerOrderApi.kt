package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.CustomerOrder
import com.smartshehar.customercallingv2.models.dtos.*
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface CustomerOrderApi {

    @Headers("Accept: application/json")
    @POST("/restaurants/orders")
    suspend fun saveCustomerOrder(@Body customerOrderRq: CreateCustomerOrderRq): Response<SuccessRs<CreateCustomerOrderRs>>

    @Headers("Accept: application/json")
    @GET("/restaurants/orders/customers/{customerId}")
    suspend fun getCustomerOrders(@Path("customerId") serverCustomerId: String): Response<SuccessRs<List<GetCustomerOrderRs>>>

    @GET("/restaurants/orders/customers/{customerId}/orderdetails/{orderId}")
    suspend fun getOrderItemsFromCustomerOrder(
        @Path("customerId") serverCustomerId: String,
        @Path("orderId") orderId: String
    ): Response<SuccessRs<List<GetOrderItemsRs.OrderItemRs>>>

}