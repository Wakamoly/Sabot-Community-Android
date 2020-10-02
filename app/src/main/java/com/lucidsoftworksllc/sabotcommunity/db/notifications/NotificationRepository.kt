package com.lucidsoftworksllc.sabotcommunity.db.notifications

import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class NotificationRepository
constructor(
        private val notificationDao: NotificationDao,
        private val notificationRetrofit: NotificationRetrofit,
        private val cacheMapper: NotificationCacheMapper,
        private val networkMapper: NotificationNetworkMapper
){
    suspend fun getNotification(): Flow<DataState<List<NotificationDataModel>>> = flow {
        emit(DataState.Loading)
        delay(1000)
        try {
            val networkBlogs = notificationRetrofit.get()
            val blogs = networkMapper.mapFromEntityList(networkBlogs)
            for (blog in blogs){
                notificationDao.insert(cacheMapper.mapToEntity(blog))
            }
            val cachedBlogs = notificationDao.get()
            emit(DataState.Success(cacheMapper.mapFromEntityList(cachedBlogs)))
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }
}