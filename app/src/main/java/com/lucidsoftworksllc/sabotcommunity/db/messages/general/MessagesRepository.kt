package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository
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
) : BaseRepository() {
    /*suspend fun getMessages(dUsername: String, dUserID: Int): Flow<DataState<List<MessagesDataModel>>> = flow {
        emit(DataState.Loading)
        try {
            val cachedMessages = messagesDao.get()
            if (cachedMessages.isEmpty()){
                val networkMessages = messagesRetrofit.getMessages(dUsername, dUserID)
                //val messages = networkMapper.mapFromEntityList(networkMessages)
                for (message in messages){
                    messagesDao.insert(cacheMapper.mapToEntity(message))
                }
                messagesDao.setAllLoaded()
                val newCachedMessages = messagesDao.get()
                emit(DataState.UpdateSuccess(cacheMapper.mapFromEntityList(newCachedMessages)))
            }else{
                emit(DataState.Success(cacheMapper.mapFromEntityList(cachedMessages)))
                val networkMessages = messagesRetrofit.getMessages(dUsername, dUserID)
                //val messages = networkMapper.mapFromEntityList(networkMessages)
                for (message in messages){
                    if (message.type != "group"){
                        val rowExists = messagesDao.isRowExist(message.sent_by)
                        if (rowExists.isNotEmpty()){
                            message.id = rowExists[0].id
                            messagesDao.updateRow(cacheMapper.mapToEntity(message))
                        }else {
                            messagesDao.insert(cacheMapper.mapToEntity(message))
                        }
                    }else{
                        val rowExists = messagesDao.isRowExistGroup(message.group_id)
                        if (rowExists.isNotEmpty()){
                            message.id = rowExists[0].id
                            messagesDao.updateRow(cacheMapper.mapToEntity(message))
                        }else {
                            messagesDao.insert(cacheMapper.mapToEntity(message))
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
    }*/

}