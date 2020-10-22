package com.lucidsoftworksllc.sabotcommunity.network

import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.CurrentPublicsModel
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.ProfileTopModel
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UsersOnlineModel2
import retrofit2.http.*

interface ProfileApi {


    @GET("profiletop_api.php")
    suspend fun getProfileTop(
            @Query("userid") dUserID: Int?,
            @Query("userid2") profileUserId: Int?,
            @Query("deviceusername") dUsername: String?
    ): ProfileTopModel

    /*@FormUrlEncoded
    @POST("dashboardfeed_api.php")
    suspend fun getDashFeed(
            @Field("page") page: Int?,
            @Field("items") items: Int?,
            @Field("username") username: String?,
            @Field("userid") userid: Int?,
            @Field("method") method: String?
    ): List<ProfilenewsRecycler>*/


    @GET("profilenews_api.php")
    suspend fun getProfileNews(
            @Query("userid") dUserID: Int?,
            @Query("userprofileid") userProfileId: Int?,
            @Query("thisusername") dUsername: String?
    ): List<ProfilenewsRecycler>


    /*@GET("num_users_online.php")
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
    )*/

}