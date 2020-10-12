package com.lucidsoftworksllc.sabotcommunity.db.messages.user_messages

import androidx.room.*

@Dao
interface UserMessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addMessage(message: UserMessagesEntity)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateMessage(message: UserMessagesEntity)

    @Delete
    suspend fun deleteMessage(message: UserMessagesEntity)

    @Query("SELECT * FROM messages_user WHERE (user_to = :username AND user_from = :deviceUser) OR (user_from = :username AND user_to = :deviceUser) ORDER BY message_id DESC")
    suspend fun getMessages(username: String, deviceUser: String): List<UserMessagesEntity>

    @Query("SELECT EXISTS(SELECT message_id FROM messages_user WHERE message_id = :id)")
    suspend fun isRowExist(id: Int): Boolean

}