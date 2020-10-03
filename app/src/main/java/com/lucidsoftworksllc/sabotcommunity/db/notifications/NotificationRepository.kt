package com.lucidsoftworksllc.sabotcommunity.db.notifications

import android.content.Context
import androidx.fragment.app.FragmentContainer
import com.lucidsoftworksllc.sabotcommunity.others.deviceUserID
import com.lucidsoftworksllc.sabotcommunity.others.deviceUsername
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
    suspend fun getNotification(mContext: Context): Flow<DataState<List<NotificationDataModel>>> = flow {
        emit(DataState.Loading)
        try {
            val cachedNotis = notificationDao.get()
            if (cachedNotis.isEmpty()){
                val networkNotis = notificationRetrofit.getNotifications(1,200,mContext.deviceUsername,mContext.deviceUserID)
                val notis = networkMapper.mapFromEntityList(networkNotis)
                for (noti in notis){
                    notificationDao.insert(cacheMapper.mapToEntity(noti))
                }
                emit(DataState.Success(cacheMapper.mapFromEntityList(notificationDao.get())))
            }else{
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedNotis)))
                val networkNotis = notificationRetrofit.getNotifications(1,200,mContext.deviceUsername,mContext.deviceUserID)
                val notis = networkMapper.mapFromEntityList(networkNotis)
                for (noti in notis){
                    notificationDao.insert(cacheMapper.mapToEntity(noti))
                }
                val newCachedNotis = notificationDao.getUnopened()
                if (newCachedNotis.isNotEmpty()){
                    emit(DataState.UpdateSuccess(cacheMapper.mapFromEntityList(newCachedNotis)))
                }

            }

        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }
}