package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Query

interface MessagesRetrofit {

    @GET("messages.php/convos")
    suspend fun getMessages(
            @Query("username") username: String?,
            @Query("userid") userid: Int?
    ): List<MessagesCacheEntity>
}