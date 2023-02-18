package com.smartshehar.customercallingv2.repositories.api

import com.smartshehar.customercallingv2.models.Owner
import com.smartshehar.customercallingv2.models.dtos.LoginRq
import com.smartshehar.customercallingv2.models.dtos.LoginRs
import com.smartshehar.customercallingv2.models.dtos.SuccessRs
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface AuthApi {

    @POST("v1/auth/owners/login/")
    suspend fun loginOwner(@Body loginRq: LoginRq): Response<LoginRs>

    @POST("v1/auth/owners/register/")
    suspend fun registerOwner(@Body loginRq: LoginRq): Response<LoginRs>

    @GET("accounts/profile")
    suspend fun getOwnerProfile() : Response<SuccessRs<Owner>>


}