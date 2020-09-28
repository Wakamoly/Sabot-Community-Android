package com.lucidsoftworksllc.sabotcommunity.retrofit

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface UserApiInterface {
    @GET("getUsers_api.php")
    fun getUsers(
            @Query("item_type") item_type: String?,
            @Query("key") keyword: String?
    ): Call<List<User?>?>?
}