package com.lucidsoftworksllc.sabotcommunity.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationCacheEntity
import com.lucidsoftworksllc.sabotcommunity.db.notifications.NotificationDao
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsDao
import com.lucidsoftworksllc.sabotcommunity.db.publics.PublicsEntity

@Database(
        entities = [PublicsEntity::class, NotificationCacheEntity::class],
        version = 2)
abstract class SabotDatabase: RoomDatabase() {

    abstract fun getPublicsDao() : PublicsDao
    abstract fun getNotificationsDao() : NotificationDao
    //abstract fun getMessagesDao() : MessagesDao
    //abstract fun getUserMessagesDao() : UserMessagesDao
    //abstract fun getTypedMessageDao() : TypedMessageDao

    companion object {
        val DATABASE_NAME: String = "sabotdatabase"
        @Volatile private var instance : SabotDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance?: synchronized(LOCK){
            instance ?: buildDatabase(context).also {
                instance = it
            }
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
                context.applicationContext,
                SabotDatabase::class.java,
                "sabotdatabase"
        ).fallbackToDestructiveMigration().build()

    }
}