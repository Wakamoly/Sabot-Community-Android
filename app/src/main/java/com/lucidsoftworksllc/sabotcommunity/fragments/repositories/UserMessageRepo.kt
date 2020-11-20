package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesEntity
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.SentMessageResponse
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessageData
import com.lucidsoftworksllc.sabotcommunity.models.network_autogen.UserMessagesFromID
import com.lucidsoftworksllc.sabotcommunity.network.UserMessageApi
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository
import com.lucidsoftworksllc.sabotcommunity.others.currentDate
import com.lucidsoftworksllc.sabotcommunity.others.currentDateSendMessage
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import org.json.JSONException
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.util.*
import kotlin.collections.ArrayList

class UserMessageRepo (
        private val api: UserMessageApi,
        private val typedMessageDao: TypedMessageDao,
        private val messagesDao: UserMessagesDao,
        private val messageUserInfoDao: MessageUserInfoDao
) : BaseRepository() {

    suspend fun getTypedMessage(userTo: String) : String {
        var returnString = ""
        if (typedMessageDao.isRowExistUser(userTo)){
            returnString = typedMessageDao.getTypedMessageUser(userTo)
        }
        return returnString
    }

    suspend fun setTypedMessage(string: String, userTo: String) {
        if (typedMessageDao.isRowExistUser(userTo)){
            typedMessageDao.updateTypedMessageUser(string,userTo)
        }else{
            val typedMessage = TypedMessageEntity("user",userTo,0, string)
            typedMessageDao.insertTypedMessage(typedMessage)
        }
    }

    suspend fun isUserInfoRetrievable(userTo: String) : Boolean {
        return messageUserInfoDao.isRowExistUsername(userTo)
    }

    suspend fun getUserInfoDB(userTo: String) = safeDatabaseCall { messageUserInfoDao.getUserInfo(userTo) }

    suspend fun getUserInfoNET(userTo: String, dUsername: String, dUserID: Int) : DataState<MessageUserInfoEntity> {
        val netEntity = safeApiCall { api.getUserInfoNET(userTo, dUsername, dUserID)  }
        if (netEntity is DataState.Success){
            val netEntityData = netEntity.data
            if (messageUserInfoDao.isRowExist(netEntityData.user_id)) {
                messageUserInfoDao.updateUser(netEntityData)
            } else {
                messageUserInfoDao.addUser(netEntityData)
            }
        }
        return netEntity
    }

    suspend fun isUserMessagesRetrievable(userTo: String, deviceUser: String) : Boolean {
        return messagesDao.isRowExistUsername(userTo, deviceUser)
    }

    suspend fun getUserMessagesDB(userTo: String, deviceUser: String) : DataState<UserMessageData> {
        val dbList = safeDatabaseCall { messagesDao.getMessages(userTo, deviceUser) }
        val array: ArrayList<UserMessagesEntity> = ArrayList()
        if (dbList is DataState.Success){
            val data = dbList.data
            array.addAll(data)
        }
        val userMessages = UserMessageData(false, array)
        return try{
            DataState.Success(userMessages)
        }catch (throwable: Throwable){
            DataState.Failure(false, null, null)
        }
    }

    suspend fun getUserMessagesNET(userTo: String, dUsername: String, dUserID: Int) : DataState<UserMessageData> {
        val netEntityList = safeApiCall { api.getUserMessagesNET(dUsername, userTo, dUserID)  }
        if (netEntityList is DataState.Success){
            val netEntityData = netEntityList.data.messages
            for (data in netEntityData){
                if (messagesDao.isRowExist(data.message_id)) {
                    messagesDao.updateMessage(data)
                } else {
                    messagesDao.addMessage(data)
                }
            }
        }
        return netEntityList
    }

    suspend fun getNewUserMessagesNET(userTo: String, dUsername: String, dUserID: Int, lastId: Int) : DataState<UserMessagesFromID> {
        val netEntityList = safeApiCall { api.getNewUserMessagesNET(dUsername, userTo, dUserID, lastId)  }
        if (netEntityList is DataState.Success){
            val netEntityData = netEntityList.data.messages
            for (data in netEntityData){
                if (messagesDao.isRowExist(data.message_id)) {
                    messagesDao.updateMessage(data)
                } else {
                    messagesDao.addMessage(data)
                }
            }
        }
        return netEntityList
    }

    /*suspend fun sendMessageOLD(userTo: String, dUsername: String, dUserID: Int, message: String, image: Bitmap?) : DataState<UserMessageData> {
        val sentMessageData = safeApiCall { api.sendMessage(userTo, dUsername, dUserID, message) }
        if (sentMessageData is DataState.Success){
            val data = sentMessageData.data
            val messageEntity = UserMessagesEntity(data.messageid, userTo, dUsername, message, currentDateSendMessage(), "")
            return if (image != null){
                sendMessageImage(image, messageEntity, dUsername)
            }else{
                val messageData = UserMessageData(false, listOf(messageEntity))
                if (messagesDao.isRowExist(messageEntity.message_id)) {
                    messagesDao.updateMessage(messageEntity)
                } else {
                    messagesDao.addMessage(messageEntity)
                }
                try {
                    DataState.Success(messageData)
                }catch (throwable: Throwable){
                    DataState.Failure(false, null, null)
                }
            }
        }else{
            return DataState.Failure(false, null, null)
        }
    }*/

    suspend fun sendMessage(userTo: String, dUsername: String, dUserID: Int, message: String, image: Bitmap?) : DataState<UserMessageData> {
        var jsonObject: JSONObject? = null
        if (image != null){
            jsonObject = prepareImageJSON(image, dUsername)
        }

        val sentMessageData = safeApiCall { api.sendMessage(userTo, dUsername, dUserID, message, jsonObject) }
        if (sentMessageData is DataState.Success){
            if (!sentMessageData.data.error){
                val data = sentMessageData.data
                val messageEntity = UserMessagesEntity(data.messageid, userTo, dUsername, message, currentDateSendMessage(), data.imagepath)
                return try {
                    val messageData = UserMessageData(false, listOf(messageEntity))
                    if (messagesDao.isRowExist(messageEntity.message_id)) {
                        messagesDao.updateMessage(messageEntity)
                    } else {
                        messagesDao.addMessage(messageEntity)
                    }
                    DataState.Success(messageData)
                }catch (throwable: Throwable){
                    DataState.Failure(false, null, null, "Network response error!")
                }
            }else{
                return DataState.Failure(false, null, null, "Network response error!")
            }

        }else{
            return DataState.Failure(false, null, null)
        }
    }


    private fun prepareImageJSON(bitmap: Bitmap, dUsername: String) : JSONObject {
        val jsonObject = JSONObject()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject.put("name", imgname)
            jsonObject.put("image", encodedImage)
            jsonObject.put("user_from", dUsername)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        return jsonObject
    }


    /*private suspend fun sendMessageImageOLD(bitmap: Bitmap, messageEntity: UserMessagesEntity, dUsername: String) : DataState<UserMessageData> {
        val jsonObject = JSONObject()
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream)
        val encodedImage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
        try {
            val imgname = Calendar.getInstance().timeInMillis.toString()
            jsonObject.put("name", imgname)
            jsonObject.put("message_id", messageEntity.message_id)
            jsonObject.put("image", encodedImage)
            jsonObject.put("user_from", dUsername)
        } catch (e: JSONException) {
            Log.e("JSONObject Here", e.toString())
        }
        var messageData = UserMessageData(false, listOf(messageEntity))
        val result = safeApiCall { api.sendMessageImage(jsonObject) }
        if (result is DataState.Success){
            if (!result.data.error){
                val newMessageEntity = UserMessagesEntity(messageEntity.message_id, messageEntity.user_to, messageEntity.user_from, messageEntity.body, messageEntity.date, result.data.imagepath)
                if (messagesDao.isRowExist(newMessageEntity.message_id)) {
                    messagesDao.updateMessage(newMessageEntity)
                } else {
                    messagesDao.addMessage(newMessageEntity)
                }
                messageData = UserMessageData(false, listOf(newMessageEntity))
            }
        }
        return try {
            DataState.Success(messageData)
        }catch (throwable: Throwable){
            DataState.Failure(false, null, null)
        }
    }*/

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