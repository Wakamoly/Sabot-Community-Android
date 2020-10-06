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
            }else{
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedMessages)))
                val networkMessages = messagesRetrofit.getMessages(mContext.deviceUsername,mContext.deviceUserID)
                val messages = networkMapper.mapFromEntityList(networkMessages)
                for (message in messages){
                    if (message.type != "group"){
                        val queryString = "SELECT id FROM messages_general WHERE user_from = '${message.user_from}' AND type != 'group' LIMIT 1"
                        val rowID = messagesDao.isRowExist(SimpleSQLiteQuery(queryString))
                        println("ROWID: $rowID")
                        if (rowID > 0){
                            message.id = rowID
                            messagesDao.updateRow(cacheMapper.mapToEntity(message))
                            println("UPDATING GROUP ROW $rowID, message.id: ${message.id}")
                        }else {
                            messagesDao.insert(cacheMapper.mapToEntity(message))
                            println("INSERTING GROUP NEW ID: ${message.id}")
                        }
                    }else{
                        val queryString = "SELECT id FROM messages_general WHERE group_id = ${message.group_id} AND type = 'group' LIMIT 1"
                        val rowID = messagesDao.isRowExist(SimpleSQLiteQuery(queryString))
                        println("ROWID: $rowID")
                        if (rowID > 0){
                            message.id = rowID
                            messagesDao.updateRow(cacheMapper.mapToEntity(message))
                            println("UPDATING USER ROW $rowID, message.id: ${message.id}")
                        }else {
                            messagesDao.insert(cacheMapper.mapToEntity(message))
                            println("INSERTING USER NEW ID: ${message.id}")
                        }
                    }

                }
                val newCachedMessages = messagesDao.getUnopened()
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