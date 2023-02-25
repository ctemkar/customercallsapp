package com.smartshehar.customercallingv2.di

import android.app.Application
import android.util.Log
import com.smartshehar.customercallingv2.repositories.api.AuthApi
import com.smartshehar.customercallingv2.repositories.api.CustomerApi
import com.smartshehar.customercallingv2.repositories.api.MenuItemApi
import com.smartshehar.customercallingv2.repositories.api.RestaurantApi
import com.smartshehar.customercallingv2.repositories.retrofit.NetworkConnectionInterceptor
import com.smartshehar.customercallingv2.utils.states.AuthState
import com.smartshehar.customercallingv2.utils.states.RestaurantState
import dagger.hilt.InstallIn
import dagger.Module
import dagger.Provides
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.Request
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    const val BASE_URL =
        "http://customercallingserver-env.eba-rb3az6gq.us-east-1.elasticbeanstalk.com/"
    val TAG = "ApiModule"


    @Provides
    fun provideInterceptor(authState: AuthState, application: Application): OkHttpClient {
        Log.d(TAG, "provideInterceptor: ")
        return OkHttpClient.Builder().connectTimeout(25, TimeUnit.SECONDS)
            .readTimeout(25, TimeUnit.SECONDS).addInterceptor { chain ->
                val newRequest: Request = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer ${authState.getCurrentUserToken()}")
                    .build()
                chain.proceed(newRequest)
            }
            .addInterceptor(NetworkConnectionInterceptor(application.applicationContext))
            .build()
    }

    @Provides
    @Singleton
    fun provideAuthState(application: Application): AuthState {
        return AuthState(application)
    }

    @Provides
    @Singleton
    fun provideRestaurantState(application: Application): RestaurantState {
        return RestaurantState(application)
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .client(client)
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }


    @Provides
    fun provideLoginApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }

    @Provides
    fun provideRestaurantApi(retrofit: Retrofit): RestaurantApi {
        return retrofit.create(RestaurantApi::class.java)
    }

    @Provides
    fun provideCustomerApi(retrofit: Retrofit): CustomerApi {
        return retrofit.create(CustomerApi::class.java)
    }

    @Provides
    fun provideMenuApi(retrofit: Retrofit): MenuItemApi {
        return retrofit.create(MenuItemApi::class.java)
    }
}