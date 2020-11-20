package com.lucidsoftworksllc.sabotcommunity.db.notifications

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationEntity: NotificationCacheEntity): Long

    @Query("SELECT * FROM notifications WHERE deleted = '' ORDER BY id DESC")
    suspend fun get(): List<NotificationCacheEntity>

    @Query("SELECT EXISTS(SELECT * FROM notifications WHERE deleted = '' ORDER BY id DESC LIMIT 1)")
    suspend fun isRetrievable(): Boolean

    @Query("SELECT * FROM notifications WHERE opened = 'no' AND deleted = '' ORDER BY id DESC")
    suspend fun getUnopened(): List<NotificationCacheEntity>

    @Query("UPDATE notifications SET opened = 'yes', viewed = 'yes'")
    suspend fun setAllOpened()

    @Query("UPDATE notifications SET opened = 'yes', viewed = 'yes' WHERE id = :id")
    suspend fun setOpened(id: Int)
}