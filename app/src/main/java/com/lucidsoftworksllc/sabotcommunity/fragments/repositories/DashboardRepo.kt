package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import com.lucidsoftworksllc.sabotcommunity.network.DashApi
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository

class DashboardRepo (
        private val api: DashApi
) : BaseRepository() {

    suspend fun getAds(dUsername: String) = safeApiCall {
        api.getAds(dUsername)
    }

    suspend fun getFeed(page: Int, items: Int, username: String, userid: Int, method: String) = safeApiCall {
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
    }

}