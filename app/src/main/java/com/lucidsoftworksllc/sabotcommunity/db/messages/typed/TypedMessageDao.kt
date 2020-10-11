package com.lucidsoftworksllc.sabotcommunity.db.messages.typed

import androidx.room.*

@Dao
interface TypedMessageDao {

    @Insert
    suspend fun insertTypedMessage(message: TypedMessageEntity)

    @Query("UPDATE typedmessageentity SET message = :message WHERE type = 'user' AND username = :username")
    suspend fun updateTypedMessageUser(message: String, username: String)

    @Query("UPDATE typedmessageentity SET message = :message WHERE type = 'group' AND group_id = :group_id")
    suspend fun updateTypedMessageGroup(message: String, group_id: Int)

    @Query("SELECT message FROM typedmessageentity WHERE type = 'user' AND username = :username LIMIT 1")
    suspend fun getTypedMessageUser(username: String): String

    @Query("SELECT message FROM typedmessageentity WHERE type = 'group' AND group_id = :group_id LIMIT 1")
    suspend fun getTypedMessageGroup(group_id: Int): String

    @Query("SELECT EXISTS(SELECT message FROM typedmessageentity WHERE type = 'user' AND username = :username LIMIT 1)")
    suspend fun isRowExistUser(username: String): Boolean

    @Query("SELECT EXISTS(SELECT message FROM typedmessageentity WHERE type = 'group' AND group_id = :group_id LIMIT 1)")
    suspend fun isRowExistGroup(group_id: Int): Boolean

}