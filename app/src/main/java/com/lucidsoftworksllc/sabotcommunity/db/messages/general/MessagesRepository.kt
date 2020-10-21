package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import android.content.Context
import androidx.sqlite.db.SimpleSQLiteQuery
import com.lucidsoftworksllc.sabotcommunity.others.deviceUserID
import com.lucidsoftworksllc.sabotcommunity.others.deviceUsername
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class MessagesRepository
constructor(
        private val messagesDao: MessagesDao,
        private val messagesRetrofit: MessagesRetrofit,
        private val cacheMapper: MessagesCacheMapper,
        private val networkMapper: MessagesNetworkMapper
){
    suspend fun getMessages(mContext: Context): Flow<DataState<List<MessagesDataModel>>> = flow {
        emit(DataState.Loading)
        try {
            val cachedMessages = messagesDao.get()
            if (cachedMessages.isEmpty()){
                val networkMessages = messagesRetrofit.getMessages(mContext.deviceUsername,mContext.deviceUserID)
                val messages = networkMapper.mapFromEntityList(networkMessages)
                for (message in messages){
                    messagesDao.insert(cacheMapper.mapToEntity(message))
                }
                messagesDao.setAllLoaded()
                val newCachedMessages = messagesDao.get()
                emit(DataState.UpdateSuccess(cacheMapper.mapFromEntityList(newCachedMessages)))
            }else{
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedMessages)))
                val networkMessages = messagesRetrofit.getMessages(mContext.deviceUsername,mContext.deviceUserID)
                val messages = networkMapper.mapFromEntityList(networkMessages)
                for (message in messages){
                    if (message.type != "group"){
                        val rowExists = messagesDao.isRowExist(message.sent_by)
                        if (rowExists.isNotEmpty()){
                            message.id = rowExists[0].id
                            messagesDao.updateRow(cacheMapper.mapToEntity(message))
                            //println("UPDATING USER ROW message.id: ${message.id}, ${message.sent_by}")
                        }else {
                            messagesDao.insert(cacheMapper.mapToEntity(message))
                            //println("INSERTING USER NEW ID: ${message.id}, ${message.sent_by}")
                        }
                    }else{
                        val rowExists = messagesDao.isRowExistGroup(message.group_id)
                        if (rowExists.isNotEmpty()){
                            message.id = rowExists[0].id
                            messagesDao.updateRow(cacheMapper.mapToEntity(message))
                            //println("UPDATING GROUP ROW message.id: ${message.id}, ${message.group_id}")
                        }else {
                            messagesDao.insert(cacheMapper.mapToEntity(message))
                            //println("INSERTING GROUP NEW ID: ${message.id}, ${message.group_id}")
                        }
                    }

                }
                val newCachedMessages = messagesDao.get()
                messagesDao.setAllLoaded()
                if (newCachedMessages.isNotEmpty()){
                    emit(DataState.UpdateSuccess(cacheMapper.mapFromEntityList(newCachedMessages)))
                }

            }

        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

}