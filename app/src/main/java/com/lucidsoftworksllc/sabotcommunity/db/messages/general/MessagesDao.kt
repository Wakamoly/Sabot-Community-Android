package com.lucidsoftworksllc.sabotcommunity.db.messages.general

import androidx.room.*
import androidx.sqlite.db.SimpleSQLiteQuery

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(messagesEntity: MessagesCacheEntity): Long

    @Query("SELECT * FROM messages_general ORDER BY message_id DESC")
    suspend fun get(): List<MessagesCacheEntity>

    @Query("SELECT * FROM messages_general WHERE loaded = 0 AND viewed = 'no' AND group_id = 0 ORDER BY message_id DESC")
    suspend fun getUnopened(): List<MessagesCacheEntity>

    @Query("UPDATE messages_general SET loaded = 1, viewed = 'yes' WHERE loaded = 0 AND viewed = 'no'")
    suspend fun setAllLoaded()

    @RawQuery
    suspend fun isRowExist(query: SimpleSQLiteQuery) : Int

    @RawQuery
    suspend fun getRow(query: SimpleSQLiteQuery) : Boolean

    @Update(onConflict = OnConflictStrategy.REPLACE)
    suspend fun updateRow(messagesEntity: MessagesCacheEntity)



    /*@Query("SELECT * from messages_general WHERE user_from = :user_from AND type != 'group'")
    suspend fun getMessageByUser(user_from: String) : List<MessagesCacheEntity>

    @Query("SELECT * from messages_general WHERE group_id = :group_id AND type = 'group'")
    suspend fun getMessageByUser(group_id: Int) : List<MessagesCacheEntity>

    @Update
    suspend fun updateMessageRow(messagesEntity: MessagesCacheEntity): Int

    @Query("SELECT id FROM messages_general WHERE user_from = :user_from LIMIT 1")
    suspend fun getMessageRowId(user_from: String): Int?*/



}