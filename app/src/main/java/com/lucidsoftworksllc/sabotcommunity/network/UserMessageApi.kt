package com.lucidsoftworksllc.sabotcommunity.network

import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.ProfileTopModel
import retrofit2.http.GET
import retrofit2.http.Query

interface UserMessageApi {

    //"$URL_USER_INFO?username=$user_to&deviceuser=$deviceUsername&deviceuserid=$deviceUserID"
    @GET("messages.php/user")
    suspend fun getUserInfoNET(
            @Query("username") userTo: String?,
            @Query("deviceuser") dUsername: String?,
            @Query("deviceuserid") dUserID: Int?
    ): MessageUserInfoEntity

    /*@FormUrlEncoded
    @POST("dashboardfeed_api.php")
    suspend fun getDashFeed(
            @Field("page") page: Int?,
            @Field("items") items: Int?,
            @Field("username") username: String?,
            @Field("userid") userid: Int?,
            @Field("method") method: String?
    ): List<ProfilenewsRecycler>*/


    /*@GET("profilenews_api.php")
    suspend fun getProfileNews(
            @Query("userid") dUserID: Int?,
            @Query("userprofileid") userProfileId: Int?,
            @Query("thisusername") dUsername: String?
    ): List<ProfilenewsRecycler>*/


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