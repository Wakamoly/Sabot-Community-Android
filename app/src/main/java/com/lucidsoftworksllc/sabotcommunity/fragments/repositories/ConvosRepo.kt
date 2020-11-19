package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesDataModel
import com.lucidsoftworksllc.sabotcommunity.db.messages.general.MessagesRetrofit
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessageData
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessagesFromID
import com.lucidsoftworksllc.sabotcommunity.network.UserMessageApi
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

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
                generalMessagesDao.setAllLoaded()
                val newCachedMessages = generalMessagesDao.get()
                try {
                    emit(DataState.UpdateSuccess(newCachedMessages))
                }catch (throwable: Throwable){
                    emit(DataState.Failure(false, null, null))
                }
            }else{
                emit(DataState.UpdateSuccess(cachedMessages))
                val networkMessages = api.getMessages(dUsername, dUserID)
                for (message in networkMessages){
                    if (message.type != "group"){
                        // TODO: 11/19/20 Update DB so user is not online until update
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
                val newCachedMessages = generalMessagesDao.get()
                generalMessagesDao.setAllLoaded()
                if (newCachedMessages.isNotEmpty()){
                    // TODO: 11/19/20 Emit network entity list
                    emit(DataState.UpdateSuccess(newCachedMessages))
                }

            }
        }catch (e: Exception){
            emit(DataState.Error(e))
        }
    }

}