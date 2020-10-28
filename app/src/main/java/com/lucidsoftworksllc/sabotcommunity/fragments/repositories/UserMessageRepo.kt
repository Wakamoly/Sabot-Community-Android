package com.lucidsoftworksllc.sabotcommunity.fragments.repositories

import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.typed.TypedMessageEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoDao
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_info.MessageUserInfoEntity
import com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages.UserMessagesDao
import com.lucidsoftworksllc.sabotcommunity.network.UserMessageApi
import com.lucidsoftworksllc.sabotcommunity.others.base.BaseRepository
import com.lucidsoftworksllc.sabotcommunity.util.DataState
import kotlinx.android.synthetic.main.content_chat.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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
            // TODO: 10/26/20 MAKE A NEW ENTITY TO SAVE TO DB WITH NO ONLINE TEXT OR BOOLEAN
            if (messageUserInfoDao.isRowExist(netEntityData.user_id)) {
                val newEntityData = MessageUserInfoEntity(netEntityData.user_id, netEntityData.profile_pic, netEntityData.nickname, netEntityData.verified, "", "", netEntityData.blocked_array, netEntityData.username)
                messageUserInfoDao.updateUser(newEntityData)
            } else {
                messageUserInfoDao.addUser(netEntityData)
            }
        }
        return netEntity
    }

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