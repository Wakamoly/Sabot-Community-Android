package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationDao
import com.lucidsoftworksllc.sabotcommunity.network.NotificationsApi
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository
import com.lucidsoftworksllc.sabotcommunity.util.DataState

class NotificationsRepo (
        private val api: NotificationsApi,
        private val notificationDao: NotificationDao
) : BaseRepository() {

    suspend fun isNotificationsRetrievable() : Boolean{
        return notificationDao.isRetrievable()
    }

    suspend fun getNotificationsDB(currentPage: Int, size: Int)
            : DataState<List<NotificationCacheEntity>> {
        return safeDatabaseCall { notificationDao.get() }
    }

    suspend fun getNotificationsNetwork(dUserID: Int, dUsername: String, currentPage: Int, size: Int, last_id: Int)
            : DataState<List<NotificationCacheEntity>>{
        val networkNotis = safeApiCall { api.getNotifications(currentPage, size, dUsername, dUserID, last_id) }
        if (networkNotis is DataState.Success){
            val entityData = networkNotis.data
            for (entity in entityData){
                notificationDao.insert(entity)
            }
        }
        return networkNotis
    }

    suspend fun setAllNotificationsOpened(dUsername: String){
        safeApiCall { api.setAllOpened(dUsername) }
        safeDatabaseCall { notificationDao.setAllOpened() }
    }

}