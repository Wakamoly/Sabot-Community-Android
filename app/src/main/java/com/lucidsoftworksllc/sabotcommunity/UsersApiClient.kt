package com.lucidsoftworksllc.sabotcommunity

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object UsersApiClient {
    private var retrofit: Retrofit? = null
    val apiClient: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                        .baseUrl(Constants.ROOT_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build()
            }
            return retrofit
        }
}