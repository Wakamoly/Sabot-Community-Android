package com.lucidsoftworksllc.sabotcommunity.db.notifications

import retrofit2.http.GET

interface NotificationRetrofit {

    @GET("notifications")
    suspend fun get(): List<NotificationNetworkEntity>
}