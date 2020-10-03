package com.lucidsoftworksllc.sabotcommunity.db.notifications

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface NotificationRetrofit {

    @FormUrlEncoded
    @POST("get_notifications.php")
    suspend fun getNotifications(
            @Field("page") page: Int?,
            @Field("items") items: Int?,
            @Field("username") username: String?,
            @Field("userid") userid: String?
    ): List<NotificationNetworkEntity>
}