package com.lucidsoftworksllc.sabotcommunity.network

import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.models.ProfilenewsRecycler
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.*
import org.json.JSONObject
import retrofit2.http.*

interface UserMessageApi {

    //"$URL_USER_INFO?username=$user_to&deviceuser=$deviceUsername&deviceuserid=$deviceUserID"
    @GET("messages.php/user")
    suspend fun getUserInfoNET(
            @Query("username") userTo: String?,
            @Query("deviceuser") dUsername: String?,
            @Query("deviceuserid") dUserID: Int?
    ): MessageUserInfoEntity

    //"$URL_FETCH_MESSAGES?this_user=$deviceUsername&username=$userTo&userid=$deviceUserID"
    @GET("messages.php/messages")
    suspend fun getUserMessagesNET(
            @Query("this_user") dUsername: String?,
            @Query("username") userTo: String?,
            @Query("userid") dUserID: Int?
    ): UserMessageData

    //"$URL_FETCH_MORE_MESSAGES?this_user=$deviceUsername&username=$userTo&userid=$deviceUserID&last_id=$lastId"
    @GET("messages.php/get_new_messages")
    suspend fun getNewUserMessagesNET(
            @Query("this_user") dUsername: String?,
            @Query("username") userTo: String?,
            @Query("userid") dUserID: Int?,
            @Query("last_id") lastId: Int?
    ): UserMessagesFromID


    /*
    * params["user_to"] = user_string
     *           params["message"] = message
     *           params["user_from"] = deviceUsername
     *           params["user_id"] = deviceUserID.toString()     userTo, dUsername, dUserID, message
    */
    @FormUrlEncoded
    @POST("messages.php/send")
    suspend fun sendMessage(
            @Field("user_to") userTo: String?,
            @Field("user_from") dUsername: String?,
            @Field("user_id") dUserID: Int?,
            @Field("message") message: String?
    ): SentMessageResponse


    @POST("message_image_upload.php")
    suspend fun sendMessageImage(
            @Body jsonObject: JSONObject?
    ): SentImageResponse




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