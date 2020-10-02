package com.lucidsoftworksllc.sabotcommunity.db.notifications

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(notificationEntity: NotificationCacheEntity): Long

    @Query("SELECT * FROM notifications")
    suspend fun get(): List<NotificationCacheEntity>
}