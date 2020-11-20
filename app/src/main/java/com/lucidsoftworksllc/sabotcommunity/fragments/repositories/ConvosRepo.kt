package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesRetrofit
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.lang.Exception

class ConvosRepo (
        private val api: MessagesRetrofit,
        private val generalMessagesDao: MessagesDao
) : BaseRepository() {
    suspend fun getMessages(dUsername: String, dUserID: Int): Flow<DataState<List<MessagesCacheEntity>>> = flow {
        emit(DataState.Loading)
        try {
            val cachedMessages = generalMessagesDao.get()
            if (cachedMessages.isEmpty()){
                val networkMessages = api.getMessages(dUsername, dUserID)
                for (message in networkMessages){
                    generalMessagesDao.insert(message)
                }
                try {
                    emit(DataState.Success(networkMessages))
                }catch (throwable: Throwable){
                    emit(DataState.Failure(false, null, null))
                }
                generalMessagesDao.setAllLoaded()
            }else{
                emit(DataState.Success(cachedMessages))
                val networkMessages = api.getMessages(dUsername, dUserID)
                for (message in networkMessages){
                    if (message.type != "group"){
                        val rowExists = generalMessagesDao.isRowExist(message.sent_by)
                        if (rowExists.isNotEmpty()){
                            message.id = rowExists[0].id
                            generalMessagesDao.updateRow(message)
                        }else {
                            generalMessagesDao.insert(message)
                        }
                    }else{
                        val rowExists = generalMessagesDao.isRowExistGroup(message.group_id)
                        if (rowExists.isNotEmpty()){
                            message.id = rowExists[0].id
                            generalMessagesDao.updateRow(message)
                        }else {
                            generalMessagesDao.insert(message)
                        }
                    }

                }
                if (networkMessages.isNotEmpty()){
                    emit(DataState.UpdateSuccess(networkMessages))
                }
                generalMessagesDao.setAllLoaded()

            }
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

}