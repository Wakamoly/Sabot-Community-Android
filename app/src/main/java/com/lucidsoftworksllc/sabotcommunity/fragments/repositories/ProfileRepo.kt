package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import com.lucidsoftworksllc.sabotcommunity.network.ProfileApi
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository

class ProfileRepo (
        private val api: ProfileApi
) : BaseRepository() {

    suspend fun getProfileTop(dUserID: Int, profileUserId: Int, dUsername: String) = safeApiCall {
        api.getProfileTop(dUserID, profileUserId, dUsername)
    }

    suspend fun getProfileNews(dUserID: Int, profileUserId: Int, dUsername: String) = safeApiCall {
        api.getProfileNews(dUserID, profileUserId, dUsername)
    }

    /*suspend fun getFeed(page: Int, items: Int, username: String, userid: Int, method: String) = safeApiCall {
        api.getDashFeed(page, items, username, userid, method)
    }

    suspend fun getCurrentPublics(dUsername: String, filter: String) = safeApiCall {
        api.getCurrentPublics(dUsername, filter)
    }

    suspend fun getNumOnline(dUsername: String) = safeApiCall {
        api.getNumOnline(dUsername)
    }

    suspend fun setAdViewed(adID: Int, method: String, dUserID: Int, dUsername: String) = safeApiCall {
        api.setAdViewed(adID, method, dUserID, dUsername)
    }*/

}
