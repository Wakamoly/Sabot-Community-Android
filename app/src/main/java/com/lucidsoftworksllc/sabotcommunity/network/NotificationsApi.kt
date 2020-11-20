package com.lucidsoftworksllc.sabotcommunity.network

import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationCacheEntity
import retrofit2.http.*

interface NotificationsApi {

    @FormUrlEncoded
    @POST("get_notifications.php")
    suspend fun getNotifications(
            @Field("page") page: Int?,
            @Field("items") items: Int?,
            @Field("username") username: String?,
            @Field("userid") userid: Int,
            @Field("last_id") last_id: Int
    ): List<NotificationCacheEntity>

    @FormUrlEncoded
    @POST("set_all_noti_read.php")
    suspend fun setAllOpened(
            @Field("username") dUsername: String?
    )

}