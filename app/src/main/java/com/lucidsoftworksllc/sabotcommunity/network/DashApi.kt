package com.lucidsoftworksllc.sabotcommunity.network

import com.lucidsoftworksllc.sabotcommunity.models.CurrentPublicsPOJO
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.CurrentPublicsModel
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.DashboardAdModelItem
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UsersOnlineModel2
import retrofit2.http.*

interface DashApi {

    @GET("dashboardads_api.php")
    suspend fun getAds(
            @Query("username") username: String?
    ): List<DashboardAdModelItem>

    @FormUrlEncoded
    @POST("dashboardfeed_api.php")
    suspend fun getDashFeed(
            @Field("page") page: Int?,
            @Field("items") items: Int?,
            @Field("username") username: String?,
            @Field("userid") userid: Int?,
            @Field("method") method: String?
    ): List<ProfilenewsRecycler>

    @GET("current_publics.php")
    suspend fun getCurrentPublics(
            @Query("username") username: String?,
            @Query("filter") filter: String?
    ): List<CurrentPublicsModel>


    @GET("num_users_online.php")
    suspend fun getNumOnline(
            @Query("username") username: String?
    ): UsersOnlineModel2

    @FormUrlEncoded
    @POST("dashboard_ad_interaction.php")
    suspend fun setAdViewed(
            @Field("id") adId: Int?,
            @Field("method") method: String?,
            @Field("user_id") userid: Int?,
            @Field("username") username: String?
    )

}