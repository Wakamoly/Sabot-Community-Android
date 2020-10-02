package com.lucidsoftworksllc.sabotcommunity.db.notifications

import retrofit2.http.POST
import retrofit2.http.Query

interface NotificationRetrofit {

    @POST("get_notifications.php")
    suspend fun get(
            @Query("page") page: Int?,
            @Query("items") items: Int?,
            @Query("username") username: String?,
            @Query("userid") userid: String?
    ): List<NotificationNetworkEntity>
}