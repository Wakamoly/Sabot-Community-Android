package com.lucidsoftworksllc.sabotcommunity.db.messages.user_info

import androidx.room.*

@Dao
interface MessageUserInfoDao {

    @Insert
    suspend fun addUser(user: MessageUserInfoEntity)

    @Update
    suspend fun updateUser(user: MessageUserInfoEntity)

    @Delete
    suspend fun deleteUser(user: MessageUserInfoEntity)

    @Query("SELECT * FROM messages_user_info WHERE username = :username LIMIT 1")
    suspend fun getUserInfo(username: String) : MessageUserInfoEntity

    @Query("SELECT EXISTS(SELECT user_id FROM messages_user_info WHERE user_id = :user_id LIMIT 1)")
    suspend fun isRowExist(user_id : Int) : Boolean

    @Query("SELECT EXISTS(SELECT user_id FROM messages_user_info WHERE username = :username LIMIT 1)")
    suspend fun isRowExistUsername(username: String) : Boolean

}